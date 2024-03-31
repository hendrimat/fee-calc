package com.fujitsu.trial.feecalc.service;

import com.fujitsu.trial.feecalc.exception.ForbiddenVehicleTypeException;
import com.fujitsu.trial.feecalc.logic.DeliveryFeeCalculator;
import com.fujitsu.trial.feecalc.model.City;
import com.fujitsu.trial.feecalc.model.Phenomenon;
import com.fujitsu.trial.feecalc.model.VehicleType;
import com.fujitsu.trial.feecalc.model.Weather;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CalculatorServiceTest {

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private CalculatorService calculatorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void calculateFee_Success() throws ForbiddenVehicleTypeException {
        // Arrange
        City city = City.TALLINN;
        VehicleType vehicleType = VehicleType.BIKE;
        Weather weather = new Weather();
        weather.setAirTemperature(-20.0);
        weather.setWindSpeed(15.0);
        weather.setPhenomenon(Phenomenon.LIGHT_SLEET);
        double expectedFee = 5.5;
        when(weatherService.getWeather(city)).thenReturn(weather);
        DeliveryFeeCalculator feeCalculator = spy(new DeliveryFeeCalculator()); // Partially mock the calculator
        doReturn(expectedFee).when(feeCalculator).calculateFee(vehicleType, city, weather);

        // Act
        double actualFee = calculatorService.calculateFee(vehicleType, city, weather);

        // Assert
        assertEquals(expectedFee, actualFee);
    }

    @Test
    void calculateFee_ForbiddenVehicleTypeException() {
        // Arrange
        City city = City.TALLINN;
        VehicleType vehicleType = VehicleType.BIKE;
        Weather weather = new Weather();
        weather.setWindSpeed(21.0);
        when(weatherService.getWeather(city)).thenReturn(weather);
        DeliveryFeeCalculator feeCalculator = spy(new DeliveryFeeCalculator()); // Partially mock the calculator
        doThrow(new ForbiddenVehicleTypeException("Forbidden vehicle type")).when(feeCalculator).calculateFee(vehicleType, city, weather);

        // Act & Assert
        assertThrows(ForbiddenVehicleTypeException.class, () -> calculatorService.calculateFee(vehicleType, city, weather));
    }

    // Add more test cases to cover different scenarios, such as invalid input parameters, etc.
}
