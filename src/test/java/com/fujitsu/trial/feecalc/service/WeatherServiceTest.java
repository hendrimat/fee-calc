package com.fujitsu.trial.feecalc.service;

import com.fujitsu.trial.feecalc.logic.WeatherDataFetcher;
import com.fujitsu.trial.feecalc.model.Weather;
import com.fujitsu.trial.feecalc.repository.WeatherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import static org.mockito.Mockito.*;

class WeatherServiceTest {

    @Mock
    private WeatherDataFetcher dataFetcher;

    @Mock
    private WeatherRepository weatherRepository;

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void importWeatherData() throws Exception {
        // Mocking data
        Weather weather = new Weather();
        weather.setStation("Tallinn-Harku");
        weather.setTimestamp(OffsetDateTime.ofInstant(Instant.ofEpochSecond(1711729057L), ZoneId.of("Europe/Tallinn")));
        weather.setWmo(26038);
        weather.setAirTemperature(6.1);
        weather.setWindSpeed(4.4);
        weather.setPhenomenon(null);

        when(dataFetcher.fetchData()).thenReturn(loadTestXml());

        weatherService.importWeatherData();

        verify(weatherRepository, times(1)).save(weather);
    }

    private Document loadTestXml() throws Exception {
        File file = new File("src/test/resources/test-weather-data.xml");
        InputStream inputStream = new FileInputStream(file);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(inputStream);
    }
}
