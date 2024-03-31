package com.fujitsu.trial.feecalc.logic;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

@Slf4j
@NoArgsConstructor
@Component
public class WeatherDataFetcher {
    private final String URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";

    /**
     * Fetches weather data from the specified URL and parses it into a Document object.
     *
     * @return The parsed Document object containing weather data.
     * @throws IOException                  Signals that an I/O exception of some sort has occurred.
     * @throws ParserConfigurationException Indicates a serious configuration error.
     * @throws SAXException                 Encapsulates a general SAX error or warning.
     */
    public Document fetchData() throws IOException, ParserConfigurationException, SAXException {
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

            log.debug("Received XML data: {}...", xmlData.substring(0, 150));

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource();
            inputSource.setCharacterStream(new java.io.StringReader(xmlData.toString()));
            Document document = builder.parse(inputSource);
            document.normalizeDocument();

            connection.disconnect();
            return document;
        } catch (IOException | ParserConfigurationException | SAXException e) {
            log.error("Error fetching weather data: {}", e.getMessage());
            throw e;
        }
    }

}
