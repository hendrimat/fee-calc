package com.fujitsu.trial.feecalc.service;

import com.fujitsu.trial.feecalc.exception.MissingWeatherException;
import com.fujitsu.trial.feecalc.logic.WeatherDataFetcher;
import com.fujitsu.trial.feecalc.model.City;
import com.fujitsu.trial.feecalc.model.Phenomenon;
import com.fujitsu.trial.feecalc.model.Weather;
import com.fujitsu.trial.feecalc.repository.WeatherRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherDataFetcher dataFetcher;
    private final WeatherRepository weatherRepository;
    private final Map<City, String> cityToStation = Map.of(City.TALLINN, "Tallinn-Harku", City.TARTU, "Tartu-Tõravere", City.PÄRNU, "Pärnu");

    /**
     * Retrieves the most recent weather data from the database for the specified city.
     *
     * @param city The city for which weather data is to be retrieved.
     * @return The most recent weather data for the specified city.
     */
    public Weather getWeather(City city) {
        String station = cityToStation.get(city);
        return weatherRepository.findFirstByStationOrderByTimestampDesc(station)
                .orElseThrow(MissingWeatherException::new);
    }


    /**
     * Retrieves the most recent weather data from the database for the specified city before the specified time up to 15 minutes.
     *
     * @param city     The city for which weather data is to be retrieved.
     * @param dateTime The time for which weather data is to be retrieved.
     * @return The most recent weather data for the specified city before the given time.
     */
    public Weather getWeather(City city, OffsetDateTime dateTime) {
        String station = cityToStation.get(city);
        return weatherRepository.findMostRecentBeforeTimestamp(station, dateTime.minusMinutes(15), dateTime)
                .orElseThrow(MissingWeatherException::new);
    }


    /**
     * Imports weather data from ilmateenistus.ee and saves it to the database.
     * This method is scheduled to run every hour at HH:15:00.
     * It fetches weather data using the {@link WeatherDataFetcher} and saves it to the
     * database using the {@link WeatherRepository}.
     */
    @Value("${custom.cron.expression}")
    @Scheduled(cron = "${custom.cron.expression}") // DEFAULT: Runs every hour at HH:15:00
    public void importWeatherData() {
        Collection<String> stations = cityToStation.values();

        Document weatherData;
        try {
            weatherData = dataFetcher.fetchData();
            if (weatherData == null) {
                log.error("Error occurred while importing weather data: weather data is empty");
                return;
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            log.error("Error occurred while importing weather data: {}", e.getMessage());
            return;
        }

        Element observations = (Element) weatherData.getElementsByTagName("observations").item(0);
        String timestamp = observations.getAttribute("timestamp");
        OffsetDateTime dateTime = convertTimestamp(timestamp);

        NodeList stationList = weatherData.getElementsByTagName("station");
        for (int i = 0; i < stationList.getLength(); i++) {
            Weather weather = new Weather();
            Element station = (Element) stationList.item(i);

            String name = station.getElementsByTagName("name").item(0).getTextContent();
            if (!stations.contains(name)) continue;
            String wmo = station.getElementsByTagName("wmocode").item(0).getTextContent();
            String airTemperature = station.getElementsByTagName("airtemperature").item(0).getTextContent();
            String windSpeed = station.getElementsByTagName("windspeed").item(0).getTextContent();
            String phenomenon = station.getElementsByTagName("phenomenon").item(0).getTextContent();

            weather.setTimestamp(dateTime);
            weather.setStation(name);
            if (!wmo.isBlank()) weather.setWmo(Integer.valueOf(wmo));
            if (!airTemperature.isBlank()) weather.setAirTemperature(Double.parseDouble(airTemperature));
            if (!windSpeed.isBlank()) weather.setWindSpeed(Double.parseDouble(windSpeed));
            if (!phenomenon.isBlank()) weather.setPhenomenon(Phenomenon.valueOf(phenomenon.toUpperCase()));

            try {
                weatherRepository.save(weather);
                log.debug("Saved weather data for station {}: {}", name, weather);
            } catch (DataAccessException e) {
                log.error("Error saving weather data for station {}: {}", name, e.getMessage());
            }
        }
    }

    /**
     * Imports weather data from ilmateenistus.ee on application startup.
     * This method is annotated with {@code @PostConstruct}, meaning it will be executed after the bean is initialized.
     * It attempts to import weather data using the {@link #importWeatherData()} method and logs the result.
     */
    @PostConstruct
    public void importWeatherDataOnStartup() {
        log.info("Importing weather data on application startup...");
        try {
            importWeatherData();
            log.info("Weather data imported successfully.");
        } catch (Exception e) {
            log.error("Failed to import weather data on startup: {}", e.getMessage());
        }
    }


    private OffsetDateTime convertTimestamp(String timestampString) {
        long timestamp = Long.parseLong(timestampString);
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.of("Europe/Tallinn"));
    }
}
