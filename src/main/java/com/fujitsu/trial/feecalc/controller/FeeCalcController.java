package com.fujitsu.trial.feecalc.controller;

import com.fujitsu.trial.feecalc.exception.ForbiddenVehicleTypeException;
import com.fujitsu.trial.feecalc.model.City;
import com.fujitsu.trial.feecalc.model.FeeResponse;
import com.fujitsu.trial.feecalc.model.VehicleType;
import com.fujitsu.trial.feecalc.model.Weather;
import com.fujitsu.trial.feecalc.service.CalculatorService;
import com.fujitsu.trial.feecalc.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FeeCalcController {

    private final WeatherService weatherService;
    private final CalculatorService calculatorService;

    /**
     * Calculates the delivery fee based on the specified city and vehicle type.
     *
     * @param city        The name of the city. (required)
     * @param vehicleType The type of vehicle. (required)
     * @return ResponseEntity containing the calculated delivery fee.
     * @throws ResponseStatusException if an error occurs while calculating the delivery fee.
     */
    @PostMapping("/calculate-delivery-fee")
    public ResponseEntity<Object> calculateDeliveryFee(
            @RequestParam String city,
            @RequestParam String vehicleType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateTime
    ) {
        log.debug("Received request to calculate delivery fee for city '{}', vehicle type '{}', and datetime '{}'", city, vehicleType, dateTime);

        try {
            City parsedCity = City.valueOf(city.toUpperCase());
            VehicleType parsedVehicleType = VehicleType.valueOf(vehicleType.toUpperCase());
            log.debug("Parsed city: '{}', Parsed vehicle type: '{}'", parsedCity, parsedVehicleType);

            Weather weather = (dateTime != null) ? weatherService.getWeather(parsedCity, dateTime) : weatherService.getWeather(parsedCity);
            log.debug("Retrieved weather information: '{}'", weather);

            Double fee;
            try {
                fee = calculatorService.calculateFee(parsedVehicleType, parsedCity, weather);
            } catch (ForbiddenVehicleTypeException e) {
                log.debug("Calculated delivery fee: {}", e.getMessage());
                return ResponseEntity.ok(new FeeResponse(e.getMessage()));
            }
            log.debug("Calculated delivery fee: {}", fee);

            // For some reason the IntelliJ api try-out feature does not work, but sending the request using curl or in a browser works.
            // It's probably related to the CORS and WebSecurityConfig.
            return ResponseEntity.ok(new FeeResponse(fee));
        } catch (RuntimeException exception) {
            log.error("Error calculating delivery fee", exception);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error calculating delivery fee", exception);
        }
    }
}
