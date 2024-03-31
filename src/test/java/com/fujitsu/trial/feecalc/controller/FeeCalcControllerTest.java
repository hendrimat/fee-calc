package com.fujitsu.trial.feecalc.controller;

import com.fujitsu.trial.feecalc.exception.ForbiddenVehicleTypeException;
import com.fujitsu.trial.feecalc.model.City;
import com.fujitsu.trial.feecalc.model.FeeResponse;
import com.fujitsu.trial.feecalc.model.VehicleType;
import com.fujitsu.trial.feecalc.model.Weather;
import com.fujitsu.trial.feecalc.service.CalculatorService;
import com.fujitsu.trial.feecalc.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

class FeeCalcControllerTest {

    @Mock
    private WeatherService weatherService;

    @Mock
    private CalculatorService calculatorService;

    @InjectMocks
    private FeeCalcController feeCalcController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void calculateDeliveryFee_Success() {
        // Arrange
        String city = "Tallinn";
        String vehicleType = "Car";
        City parsedCity = City.TALLINN;
        VehicleType parsedVehicleType = VehicleType.CAR;
        Weather weather = new Weather(); // Create a weather object with appropriate values
        Double expectedFee = 10.0; // Set expected fee
        when(weatherService.getWeather(parsedCity)).thenReturn(weather);
        when(calculatorService.calculateFee(parsedVehicleType, parsedCity, weather)).thenReturn(expectedFee);

        // Act
        ResponseEntity<Object> responseEntity = feeCalcController.calculateDeliveryFee(city, vehicleType, null);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        // Ensure FeeResponse object with correct fee is returned
        assertEquals(expectedFee, ((FeeResponse) responseEntity.getBody()).getFee());
        // Ensure no error message is present
        assertNull(((FeeResponse) responseEntity.getBody()).getError());
    }

    @Test
    void calculateDeliveryFee_ForbiddenVehicleTypeException() {
        // Arrange
        String city = "Tallinn";
        String vehicleType = "Bike";
        City parsedCity = City.TALLINN;
        VehicleType parsedVehicleType = VehicleType.BIKE;
        Weather weather = new Weather(); // Create a weather object with appropriate values
        String expectedErrorMessage = "Forbidden vehicle type"; // Set expected error message
        when(weatherService.getWeather(parsedCity)).thenReturn(weather);
        when(calculatorService.calculateFee(parsedVehicleType, parsedCity, weather))
                .thenThrow(new ForbiddenVehicleTypeException(expectedErrorMessage));

        // Act
        ResponseEntity<Object> responseEntity = feeCalcController.calculateDeliveryFee(city, vehicleType, null);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        // Ensure FeeResponse object with correct error message is returned
        assertEquals(expectedErrorMessage, ((FeeResponse) responseEntity.getBody()).getError());
        // Ensure no fee is present
        assertNull(((FeeResponse) responseEntity.getBody()).getFee());
    }

    // Add more test cases to cover different scenarios, such as invalid input parameters, etc.
}
