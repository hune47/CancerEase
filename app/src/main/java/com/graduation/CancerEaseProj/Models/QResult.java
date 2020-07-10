package com.graduation.CancerEaseProj.Models;


public class QResult {
    private int id, domain_id, result, level;
    private String timestamp;

    public QResult() {
    }

    public QResult(int domain_id, int result, int level) {
        this.domain_id = domain_id;
        this.result = result;
        this.level = level;
    }

    public QResult(int domain_id, int result, int level, String timestamp) {
        this.domain_id = domain_id;
        this.result = result;
        this.level = level;
        this.timestamp = timestamp;
    }

    public QResult(int id, int domain_id, int result, int level, String timestamp) {
        this.id = id;
        this.domain_id = domain_id;
        this.result = result;
        this.level = level;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDomain_id() {
        return domain_id;
    }

    public void setDomain_id(int domain_id) {
        this.domain_id = domain_id;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
