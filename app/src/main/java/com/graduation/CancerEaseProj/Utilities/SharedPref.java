package com.graduation.CancerEaseProj.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.graduation.CancerEaseProj.R;

public class SharedPref {
    private Context ctx;
    private SharedPreferences default_preference;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public SharedPref(Context context) {
        this.ctx = context;
        default_preference = PreferenceManager.getDefaultSharedPreferences(context);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private String str(int string_id) {
        return ctx.getString(string_id);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void setYourName(String name) {
        default_preference.edit().putString(str(R.string.pref_title_name), name).apply();
    }
    public String getYourName() {
        return default_preference.getString(str(R.string.pref_title_name), str(R.string.default_your_name));
    }
    public void setYourEmail(String name) {
        default_preference.edit().putString(str(R.string.pref_title_email), name).apply();
    }
    public String getYourEmail() {
        return default_preference.getString(str(R.string.pref_title_email), str(R.string.default_your_email));
    }
    public void setYourMobile(String name) {
        default_preference.edit().putString(str(R.string.pref_title_mobile), name).apply();
    }
    public String getYourMobile() {
        return default_preference.getString(str(R.string.pref_title_mobile), str(R.string.default_your_mobile));
    }
    public void setYourRecordNumber(String name) {
        default_preference.edit().putString(str(R.string.pref_title_record_number), name).apply();
    }
    public String getYourRecordNumber() {
        return default_preference.getString(str(R.string.pref_title_record_number),str(R.string.default_your_record_number));
    }
    public void setYourGender(String name) {
        default_preference.edit().putString(str(R.string.pref_title_gender), name).apply();
    }
    public String getYourGender() {
        return default_preference.getString(str(R.string.pref_title_gender), str(R.string.default_your_gender));
    }
    public void setYourIdNumber(String name) {
        default_preference.edit().putString(str(R.string.pref_title_id_no), name).apply();
    }
    public String getYourIdNumber() {
        return default_preference.getString(str(R.string.pref_title_id_no), str(R.string.default_your_id_no));
    }
    public void setYourNationality(String name) {
        default_preference.edit().putString(str(R.string.pref_title_nationality), name).apply();
    }
    public String getYourNationality() {
        return default_preference.getString(str(R.string.pref_title_nationality), str(R.string.default_your_nationality));
    }
    public void setYourBirthDate(String name) {
        default_preference.edit().putString(str(R.string.ref_title_birth_date), name).apply();
    }
    public String getYourBirthDate() {
        return default_preference.getString(str(R.string.ref_title_birth_date), str(R.string.default_your_Birth_Date));
    }
    public void setYourId(String name) {
        default_preference.edit().putString(str(R.string.pref_title_id), name).apply();
    }
    public String getYourId() {
        return default_preference.getString(str(R.string.pref_title_id),str(R.string.default_your_id));
    }
    public void setYourPhoto(String name) {
        default_preference.edit().putString(str(R.string.ref_title_photo), name).apply();
    }
    public String getYourPhoto() {
        return default_preference.getString(str(R.string.ref_title_photo), str(R.string.default_your_photo));
    }
    public void setYourBloodType(String name) {
        default_preference.edit().putString(str(R.string.pref_title_blood_type), name).apply();
    }
    public String getYourBloodType() {
        return default_preference.getString(str(R.string.pref_title_blood_type), str(R.string.default_your_blood_type));
    }
    public void setAccountType(String name) {
        default_preference.edit().putString(str(R.string.pref_title_account_type), name).apply();
    }
    public String getAccountType() {
        return default_preference.getString(str(R.string.pref_title_account_type), str(R.string.default_your_account_type));
    }
    public void setYourSteps(float name) {
        default_preference.edit().putFloat(str(R.string.pref_title_steps), name).apply();
    }
    public float getYourSteps() {
        return default_preference.getFloat(str(R.string.pref_title_steps), Float.parseFloat(str(R.string.default_your_steps)));
    }
    public void setYourTemp(float name) {
        default_preference.edit().putFloat(str(R.string.pref_title_temp), name).apply();
    }
    public float getYourTemp() {
        return default_preference.getFloat(str(R.string.pref_title_temp), Float.parseFloat(str(R.string.default_your_temp)));
    }
    public void setDeviceToken(String name) {
        default_preference.edit().putString(str(R.string.pref_title_device_token), name).apply();
    }
    public String getDeviceToken() {
        return default_preference.getString(str(R.string.pref_title_device_token), str(R.string.default_your_device_token));
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
