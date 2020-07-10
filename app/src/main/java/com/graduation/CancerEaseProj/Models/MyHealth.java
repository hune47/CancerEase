package com.graduation.CancerEaseProj.Models;

public class MyHealth {
    private double value;
    private double timestamp;

    public MyHealth() {
    }

    public MyHealth(double value, double timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }
}
