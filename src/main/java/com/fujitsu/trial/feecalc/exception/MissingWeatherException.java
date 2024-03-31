package com.fujitsu.trial.feecalc.exception;

public class MissingWeatherException extends RuntimeException {

    public MissingWeatherException() {
        super("No weather data found for the specified city and time");
    }
}