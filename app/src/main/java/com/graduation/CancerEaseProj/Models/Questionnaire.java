package com.graduation.CancerEaseProj.Models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class Questionnaire {
    private int physical, psychological, social, environment;
    @ServerTimestamp
    private Timestamp timestamp;

    public Questionnaire() {
    }

    public Questionnaire(int physical, int psychological, int social, int environment) {
        this.physical = physical;
        this.psychological = psychological;
        this.social = social;
        this.environment = environment;
    }

    public int getPhysical() {
        return physical;
    }

    public void setPhysical(int physical) {
        this.physical = physical;
    }

    public int getPsychological() {
        return psychological;
    }

    public void setPsychological(int psychological) {
        this.psychological = psychological;
    }

    public int getSocial() {
        return social;
    }

    public void setSocial(int social) {
        this.social = social;
    }

    public int getEnvironment() {
        return environment;
    }

    public void setEnvironment(int environment) {
        this.environment = environment;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
