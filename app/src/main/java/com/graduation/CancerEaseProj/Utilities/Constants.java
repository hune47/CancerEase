package com.graduation.CancerEaseProj.Utilities;

import android.os.Environment;

public class Constants {
    public static final String PATIENTS_COLLECTION = "Patients";
    public static final String DOCTORS_COLLECTION = "Doctors";
    public static final String MESSAGES_COLLECTION = "Messages";
    public static final String NOTIFICATIONS_COLLECTION = "notifications";
    public static final String CHAT_GROUPS_REF = "ChatGroups";
    public static final String HEALTH_REF = "PatientsHealth";
    public static final String QUESTIONNAIRE_COLLECTION = "Questionnaire";
    public static final String CLOUD_USER_PHOTOS_DIR =  "user_images";
    public static final String DEVICE_USER_PHOTOS_PATH =  Environment.getExternalStorageDirectory().getPath()+"/CancerEase/";

    public static final int REQUEST_ENABLE_BT = 0;
    public static final int REQUEST_READ_CODE = 1;

    public static final int SUCCESS_SEND = 20;
    public static final int ERROR_SOCKET = 21;
    public static final int ERROR_CONNECT = 22;
    public static final int ERROR_BYTES = 23;
    public static final int ERROR_OUTPUT_STREAM = 24;

    public static final String UUID = "04c6093b-0000-1000-8000-00805f9b34fb";
    public static final String EMPATICA_API_KEY = "87d9dea8f52147c4a6c952751cb3213a";
    public static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    public static final String SERVER_KEY = "key=" + "AAAAnjled30:APA91bH-DwXA1tR5o49qnJsjK0t0DbzMFdyEzK5TamUNErkkRqrLx-LLXYR32ueVEeLOUjpk6M0kn0ec77ZuqjJZJ_hGzBRqP7V4bA3cEZB98AldEIeupa4H3hEiXDHRe62ExwUOrFBc";
}
