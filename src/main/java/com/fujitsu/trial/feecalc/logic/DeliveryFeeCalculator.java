package com.fujitsu.trial.feecalc.logic;

import com.fujitsu.trial.feecalc.model.City;
import com.fujitsu.trial.feecalc.model.Phenomenon;
import com.fujitsu.trial.feecalc.model.VehicleType;
import com.fujitsu.trial.feecalc.model.Weather;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeliveryFeeCalculator {
    public DeliveryFeeCalculator() {
    }

    public double calculateFee(VehicleType vehicleType, City city, Weather weather) throws IllegalArgumentException {
        double RBF = calculateRBF(vehicleType, city);
        double ATEF = calculateATEF(vehicleType, weather.getAirTemperature());
        double WSEF = calculateWSEF(vehicleType, weather.getWindSpeed());
        double WPEF = calculateWPEF(vehicleType, weather.getPhenomenon());

        return RBF + ATEF + WSEF + WPEF;
    }

    private double calculateRBF(VehicleType vehicleType, City city) {
        double RBF;
        switch (city) {
            case TALLINN -> RBF = switch (vehicleType) {
                case CAR -> 4;
                case SCOOTER -> 3.5;
                case BIKE -> 3;
            };
            case TARTU -> RBF = switch (vehicleType) {
                case CAR -> 3.5;
                case SCOOTER -> 3;
                case BIKE -> 2.5;
            };
            case PÄRNU -> RBF = switch (vehicleType) {
                case CAR -> 3;
                case SCOOTER -> 2.5;
                case BIKE -> 2;
            };
            default -> throw new IllegalArgumentException("Invalid city: " + city);
        }
        return RBF;
    }

    private double calculateATEF(VehicleType vehicleType, double airTemp) {
        double ATEF = 0;
        if (vehicleType == VehicleType.SCOOTER || vehicleType == VehicleType.BIKE) {
            if (airTemp < -10) ATEF = 1;
            else if (airTemp < 0) ATEF = 0.5;
        }
        return ATEF;
    }

    private double calculateWSEF(VehicleType vehicleType, double windSpeed) {
        double WSEF = 0;
        if (vehicleType == VehicleType.BIKE) {
            if (windSpeed >= 10 && windSpeed <= 20) WSEF = 0.5;
            else if (windSpeed > 20) throw new RuntimeException("Usage of selected vehicle type is forbidden");
        }
        return WSEF;
    }

    private double calculateWPEF(VehicleType vehicleType, Phenomenon phenomenon) {
        double WPEF = 0;
        if (vehicleType == VehicleType.SCOOTER || vehicleType == VehicleType.BIKE) {
            WPEF = switch (phenomenon) {
                case BLOWING_SNOW, DRIFTING_SNOW, HEAVY_SNOW_SHOWER, HEAVY_SNOWFALL, LIGHT_SNOW_SHOWER, LIGHT_SNOWFALL, MODERATE_SNOW_SHOWER, MODERATE_SNOWFALL, LIGHT_SLEET, MODERATE_SLEET ->
                        1;
                case HEAVY_RAIN, LIGHT_RAIN, MODERATE_RAIN, HEAVY_SHOWER, LIGHT_SHOWER, MODERATE_SHOWER -> 0.5;
                case GLAZE, HAIL, THUNDER, THUNDERSTORM ->
                        throw new RuntimeException("Usage of selected vehicle type is forbidden");
                default -> 0;
            };
        }
        return WPEF;
    }

}
