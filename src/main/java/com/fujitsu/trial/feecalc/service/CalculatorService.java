package com.fujitsu.trial.feecalc.service;

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
    public double calculateFee(VehicleType vehicleType, City city, Weather weather) throws IllegalArgumentException {
        DeliveryFeeCalculator calculator = new DeliveryFeeCalculator();
        return calculator.calculateFee(vehicleType, city, weather);
    }
}
