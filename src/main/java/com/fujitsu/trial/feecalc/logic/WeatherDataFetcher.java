package com.fujitsu.trial.feecalc.logic;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

@Slf4j
@NoArgsConstructor
@Component
public class WeatherDataFetcher {
    private final String URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

    public Document fetchData() {
        Document document = null;
        try {
            URL url = URI.create(URL).toURL();

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            log.info("Connecting to URL: {}", url);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder xmlData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                xmlData.append(line);
            }
            reader.close();

            log.debug("Received XML data: {}...", xmlData.substring(0, 300));

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource();
            inputSource.setCharacterStream(new java.io.StringReader(xmlData.toString()));
            document = builder.parse(inputSource);
            document.normalizeDocument();

            connection.disconnect();
        } catch (Exception e) {
            log.error("Error fetching weather data: {}", e.getMessage());
            e.printStackTrace();
        }
        return document;
    }

}
