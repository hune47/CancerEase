package com.graduation.CancerEaseProj.Models;

public class Answer {
    private int id, question_id, answer_no, domain_id;

    public Answer() {
    }

    public Answer(int answer_no, int question_id, int domain_id) {
        this.answer_no = answer_no;
        this.question_id = question_id;
        this.domain_id = domain_id;
    }

    public Answer(int id, int answer_no, int question_id, int domain_id) {
        this.id = id;
        this.answer_no = answer_no;
        this.question_id = question_id;
        this.domain_id = domain_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public int getAnswer_no() {
        return answer_no;
    }

    public void setAnswer_no(int answer_no) {
        this.answer_no = answer_no;
    }

    public int getDomain_id() {
        return domain_id;
    }

    public void setDomain_id(int domain_id) {
        this.domain_id = domain_id;
    }
}
