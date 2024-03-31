package com.fujitsu.trial.feecalc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Weather {

    @Id
    @GeneratedValue
    private Integer id;

    @NotNull
    private String station;

    private Integer wmo;

    private Double airTemperature;

    private Double windSpeed;

    private Phenomenon phenomenon;

    @NotNull
    private OffsetDateTime timestamp;

}
