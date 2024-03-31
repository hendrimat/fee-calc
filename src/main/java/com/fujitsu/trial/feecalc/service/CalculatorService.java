package com.fujitsu.trial.feecalc.service;

import com.fujitsu.trial.feecalc.exception.ForbiddenVehicleTypeException;
import com.fujitsu.trial.feecalc.logic.DeliveryFeeCalculator;
import com.fujitsu.trial.feecalc.model.City;
import com.fujitsu.trial.feecalc.model.VehicleType;
import com.fujitsu.trial.feecalc.model.Weather;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CalculatorService {

    /**
     * Calculates the delivery fee based on the given vehicle type, city, and weather conditions.
     *
     * @param vehicleType The type of vehicle used for delivery.
     * @param city        The city where the delivery is taking place.
     * @param weather     The current weather conditions.
     * @return The calculated delivery fee based on the provided parameters.
     * @throws IllegalArgumentException      If any of the parameters are invalid or null.
     * @throws ForbiddenVehicleTypeException If usage of selected vehicle is forbidden.
     */
    public double calculateFee(VehicleType vehicleType, City city, Weather weather) throws IllegalArgumentException, ForbiddenVehicleTypeException {
        DeliveryFeeCalculator calculator = new DeliveryFeeCalculator();
        return calculator.calculateFee(vehicleType, city, weather);
    }
}
