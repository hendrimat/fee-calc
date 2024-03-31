package com.fujitsu.trial.feecalc.repository;

import com.fujitsu.trial.feecalc.model.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Integer> {
    Optional<Weather> findFirstByStationOrderByTimestampDesc(String stationName);

    @Query("SELECT w FROM Weather w " +
            "WHERE w.station = :stationName " +
            "AND w.timestamp BETWEEN :startTime AND :endTime " +
            "ORDER BY w.timestamp DESC")
    Optional<Weather> findMostRecentBeforeTimestamp(@Param("stationName") String stationName,
                                                    @Param("startTime") OffsetDateTime startTime,
                                                    @Param("endTime") OffsetDateTime endTime);
}
