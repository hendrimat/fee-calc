package com.fujitsu.trial.feecalc.model;

import lombok.Data;

@Data
public class FeeResponse {
    private Double fee;
    private String error;

    public FeeResponse(Double fee) {
        this.fee = fee;
        this.error = null;
    }

    public FeeResponse(String error) {
        this.fee = null;
        this.error = error;
    }
}
