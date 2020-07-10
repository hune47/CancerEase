package com.graduation.CancerEaseProj.Models;

import java.io.Serializable;

public class Prescription implements Serializable {
    private long  alarm;
    private int A_KEY, ed_alarm;
    private String id, pres_name, dosage;

    public Prescription() {
    }

    public Prescription(long alarm, int a_KEY, int ed_alarm, String id, String pres_name, String dosage) {
        this.alarm = alarm;
        A_KEY = a_KEY;
        this.ed_alarm = ed_alarm;
        this.id = id;
        this.pres_name = pres_name;
        this.dosage = dosage;
    }

    public long getAlarm() {
        return alarm;
    }

    public void setAlarm(long alarm) {
        this.alarm = alarm;
    }

    public int getA_KEY() {
        return A_KEY;
    }

    public void setA_KEY(int a_KEY) {
        A_KEY = a_KEY;
    }

    public int getEd_alarm() {
        return ed_alarm;
    }

    public void setEd_alarm(int ed_alarm) {
        this.ed_alarm = ed_alarm;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPres_name() {
        return pres_name;
    }

    public void setPres_name(String pres_name) {
        this.pres_name = pres_name;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
}
