package com.fujitsu.trial.feecalc.service;

import com.fujitsu.trial.feecalc.logic.WeatherDataFetcher;
import com.fujitsu.trial.feecalc.model.City;
import com.fujitsu.trial.feecalc.model.Phenomenon;
import com.fujitsu.trial.feecalc.model.Weather;
import com.fujitsu.trial.feecalc.repository.WeatherRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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

    public Weather getWeather(City city) {
        String station = cityToStation.get(city);
        return weatherRepository.findFirstByStationOrderByTimestampDesc(station);
    }

    @Scheduled(cron = "0 15 * * * *") // Runs every hour at HH:15:00
    public void importWeatherData() {
        Collection<String> stations = cityToStation.values();

        Document weatherData = dataFetcher.fetchData();
        if (weatherData == null) return;
        String data = weatherData.getDocumentElement().getTextContent();

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

            weatherRepository.save(weather);
        }
    }

    @PostConstruct
    public void importWeatherDataOnStartup() {
        log.info("Importing weather data on application startup...");
        try {
            importWeatherData();
            log.info("Weather data imported successfully.");
        } catch (Exception e) {
            log.error("Failed to import weather data on startup: {}", e.getMessage());
            e.printStackTrace();
        }
    }


    private OffsetDateTime convertTimestamp(String timestampString) {
        long timestamp = Long.parseLong(timestampString);
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.of("Europe/Tallinn"));
    }
}
