package com.graduation.CancerEaseProj.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.sql.Timestamp;

public class Health {
    private int id,  steps;
    private double resp_rate, temperature, sleep_rate;
    @ServerTimestamp
    private Timestamp timestamp;

    public Health() {
    }

    public Health(int steps, double resp_rate, double temperature, double sleep_rate) {
        this.steps = steps;
        this.resp_rate = resp_rate;
        this.temperature = temperature;
        this.sleep_rate = sleep_rate;
    }

    public Health(int id, int steps, double resp_rate, double temperature, double sleep_rate) {
        this.id = id;
        this.steps = steps;
        this.resp_rate = resp_rate;
        this.temperature = temperature;
        this.sleep_rate = sleep_rate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public double getResp_rate() {
        return resp_rate;
    }

    public void setResp_rate(double resp_rate) {
        this.resp_rate = resp_rate;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getSleep_rate() {
        return sleep_rate;
    }

    public void setSleep_rate(double sleep_rate) {
        this.sleep_rate = sleep_rate;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
