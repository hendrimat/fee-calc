package com.fujitsu.trial.feecalc.repository;

import com.fujitsu.trial.feecalc.model.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherRepository extends JpaRepository<Weather, Integer> {
    Weather findFirstByStationOrderByTimestampDesc(String stationName);
}
