package com.graduation.CancerEaseProj.Models;

public class Question {
    private int id, domain_id;
    private String  question;

    public Question() {
    }

    public Question(int domain, String question) {
        this.domain_id = domain;
        this.question = question;
    }

    public Question(int id, int domain, String question) {
        this.id = id;
        this.domain_id = domain;
        this.question = question;
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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
