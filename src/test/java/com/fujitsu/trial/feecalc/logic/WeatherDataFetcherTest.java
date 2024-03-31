package com.fujitsu.trial.feecalc.logic;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WeatherDataFetcherTest {
    @Test
    public void testFetchData() throws Exception {
        WeatherDataFetcher weatherDataFetcher = new WeatherDataFetcher();

        Document document = weatherDataFetcher.fetchData();

        assertNotNull(document);
    }
}
