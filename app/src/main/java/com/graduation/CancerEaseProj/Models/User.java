package com.graduation.CancerEaseProj.Models;

import com.google.firebase.auth.FirebaseAuth;
import com.graduation.CancerEaseProj.Utilities.SharedPref;

import java.io.Serializable;

public class User  implements Serializable {
    private String id, recordNumber, name, birthDate, gender, nationality, bloodType, mobile, email, photo, idNumber;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public User() {
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public User(String id, String recordNumber, String name, String birthDate, String gender, String nationality, String bloodType, String mobile, String email, String photo, String idNumber) {
        this.id = id;
        this.recordNumber = recordNumber;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.nationality = nationality;
        this.bloodType = bloodType;
        this.mobile = mobile;
        this.email = email;
        this.photo = photo;
        this.idNumber = idNumber;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getRecordNumber() {
        return recordNumber;
    }
    public void setRecordNumber(String recordNumber) {
        this.recordNumber = recordNumber;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getNationality() {
        return nationality;
    }
    public void setNationality(String nationality) {
        this.nationality = nationality;
    }
    public String getBloodType() {
        return bloodType;
    }
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }
    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public String getIdNumber() {
        return idNumber;
    }
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static void saveUserData(User user, SharedPref sharedPref, String type){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        sharedPref.setYourId(userId);
        sharedPref.setYourName(user.getName());
        sharedPref.setYourEmail(user.getEmail());
        sharedPref.setYourMobile(user.getMobile());
        sharedPref.setYourPhoto(userId + ".jpg");
        sharedPref.setYourBirthDate(user.getBirthDate());
        sharedPref.setYourRecordNumber(user.getRecordNumber());
        sharedPref.setYourGender(user.getGender());
        sharedPref.setYourNationality(user.getNationality());
        sharedPref.setYourIdNumber(user.getIdNumber());
        sharedPref.setYourBloodType(user.getBloodType());
        sharedPref.setAccountType(type);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
