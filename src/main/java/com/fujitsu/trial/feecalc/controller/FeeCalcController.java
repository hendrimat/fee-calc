package com.fujitsu.trial.feecalc.controller;

import com.fujitsu.trial.feecalc.model.City;
import com.fujitsu.trial.feecalc.model.VehicleType;
import com.fujitsu.trial.feecalc.model.Weather;
import com.fujitsu.trial.feecalc.service.CalculatorService;
import com.fujitsu.trial.feecalc.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FeeCalcController {

    private final WeatherService weatherService;
    private final CalculatorService calculatorService;


    @PostMapping("/calculate-delivery-fee")
    public ResponseEntity<Object> calculateDeliveryFee(@RequestParam String city, @RequestParam String vehicleType) {
        log.debug("Received request to calculate delivery fee for city '{}' and vehicle type '{}'", city, vehicleType);

        try {
            City parsedCity = City.valueOf(city.toUpperCase());
            VehicleType parsedVehicleType = VehicleType.valueOf(vehicleType.toUpperCase());
            log.debug("Parsed city: '{}', Parsed vehicle type: '{}'", parsedCity, parsedVehicleType);

            Weather weather = weatherService.getWeather(parsedCity);
            log.debug("Retrieved weather information: '{}'", weather);

            double fee = calculatorService.calculateFee(parsedVehicleType, parsedCity, weather);
            log.debug("Calculated delivery fee: {}", fee);

            return ResponseEntity.ok(fee);
        } catch (IllegalArgumentException exception) {
            log.error("Error calculating delivery fee", exception);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error calculating delivery fee", exception);
        }
    }
}
