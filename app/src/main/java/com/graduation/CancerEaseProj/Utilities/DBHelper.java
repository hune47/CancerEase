package com.graduation.CancerEaseProj.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.graduation.CancerEaseProj.Models.ChatGroup;
import com.graduation.CancerEaseProj.Models.Health;
import com.graduation.CancerEaseProj.Models.Answer;
import com.graduation.CancerEaseProj.Models.QResult;
import com.graduation.CancerEaseProj.Models.Question;
import com.graduation.CancerEaseProj.Models.UserNotification;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DBHelper extends SQLiteAssetHelper {

    private final static String DB_NAME = "cancer_ease_db.db";
    private final static int DB_VERSION = 1;
    private static SQLiteDatabase db;
    // Table Names
    private final String TABLE_HEALTH = "tbl_health";
    private final String TABLE_QUESTIONNAIRE = "tbl_questionnaire";
    private final String TABLE_NOTIFICATIONS = "tbl_notifications";
    private final String TABLE_QUESTIONNAIRE_RESULTS = "tbl_questionnaire_results";
    private final String TABLE_CHAT_GROUPS = "tbl_chat_groups";
    // Common column names
    private final String ID = "id";
    private final String DOMAIN_ID = "domain_id";
    private final String TIME_STAMP = "timestamp";
    // HEALTH TABLE column names
    private final String RESP_RATE = "resp_rate";
    private final String TEMPERATURE = "temperature";
    private final String STEPS = "steps";
    private final String SLEEP_RATE = "sleep_rate";
    // QUESTIONNAIRES TABLE column names
    private final String QUESTION = "question";
    // NOTIFICATIONS TABLE column names
    private final String LEVEL = "level";
    private final String NOTIFICATION = "notification";
    private final String RESOURCE_URL = "url";
    private final String VIDEO = "video";
    //QUESTIONNAIRE_RESULTS TABLE column names
    private final String RESULT = "result";

    //CHAT_GROUPS TABLE column names
    private final String NAME = "name";
    private final String IMAGE = "image";
    private final String ENROLLED = "enrolled";
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void openDB() {
        db = getWritableDatabase();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void closeDB() {
        db.close();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // TABLE_HEALTH CRUD
    public Health getHealth(int id) {
        openDB();
        Health health = new Health();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_HEALTH + " where id = " + id, null);
        while (cursor.moveToNext()) {
            health.setId(cursor.getInt(cursor.getColumnIndex(ID)));
            health.setResp_rate(cursor.getDouble(cursor.getColumnIndex(RESP_RATE)));
            health.setTemperature(cursor.getDouble(cursor.getColumnIndex(TEMPERATURE)));
            health.setSteps(cursor.getInt(cursor.getColumnIndex(STEPS)));
            health.setSleep_rate(cursor.getDouble(cursor.getColumnIndex(SLEEP_RATE)));
        }
        cursor.close();
        closeDB();
        return health;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public ArrayList<Health> getAllHealth() {
        ArrayList<Health> healths = new ArrayList<>();
        openDB();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_HEALTH, null);
        while (cursor.moveToNext()) {
            Health health = new Health();
            health.setId(cursor.getInt(cursor.getColumnIndex(ID)));
            health.setResp_rate(cursor.getDouble(cursor.getColumnIndex(RESP_RATE)));
            health.setTemperature(cursor.getDouble(cursor.getColumnIndex(TEMPERATURE)));
            health.setSteps(cursor.getInt(cursor.getColumnIndex(STEPS)));
            health.setSleep_rate(cursor.getDouble(cursor.getColumnIndex(SLEEP_RATE)));
            healths.add(health);
        }
        closeDB();
        return healths;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean createHealth(Health health) {
        long result = -1;
        openDB();
        ContentValues values = new ContentValues();
        values.put(RESP_RATE, health.getResp_rate());
        values.put(TEMPERATURE, health.getTemperature());
        values.put(STEPS, health.getSteps());
        values.put(SLEEP_RATE, health.getSleep_rate());
        try {
            result = db.insert(TABLE_HEALTH, null, values);

        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
        closeDB();
        return result != -1;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean updateHealth(Health health) {
        long result = 0;
        openDB();
        ContentValues values = new ContentValues();
        values.put(RESP_RATE, health.getResp_rate());
        values.put(TEMPERATURE, health.getTemperature());
        values.put(STEPS, health.getSteps());
        values.put(SLEEP_RATE, health.getSleep_rate());
        try {
            result = db.update(TABLE_HEALTH, values, ID + "=" + health.getId(), null);
        } catch (Exception e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        closeDB();
        return result != 0;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean deleteHealth(int id) {
        long result = 0;
        openDB();
        try {
            result = db.delete(TABLE_HEALTH, ID + "=" + id, null);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
        closeDB();
        return result != 0;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // TABLE_QUESTIONNAIRE CRUD
    public Question getQuestion(int id) {
        openDB();
        Question question = new Question();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_QUESTIONNAIRE + " where id = " + id, null);
        while (cursor.moveToNext()) {
            question.setId(cursor.getInt(cursor.getColumnIndex(ID)));
            question.setQuestion(cursor.getString(cursor.getColumnIndex(QUESTION)));
            question.setDomain_id(cursor.getInt(cursor.getColumnIndex(DOMAIN_ID)));
        }
        cursor.close();
        closeDB();
        return question;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public ArrayList<Question> getAllQuestions() {
        ArrayList<Question> questions = new ArrayList<>();
        openDB();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_QUESTIONNAIRE, null);
        while (cursor.moveToNext()) {
            Question question = new Question();
            question.setId(cursor.getInt(cursor.getColumnIndex(ID)));
            question.setQuestion(cursor.getString(cursor.getColumnIndex(QUESTION)));
            question.setDomain_id(cursor.getInt(cursor.getColumnIndex(DOMAIN_ID)));
            questions.add(question);
        }
        closeDB();
        return questions;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // TABLE_TABLE_NOTIFICATIONS CRUD
    public UserNotification getNotification(int id) {
        openDB();
        UserNotification userNotification = new UserNotification();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS + " where id = " + id, null);
        while (cursor.moveToNext()) {
            userNotification.setId(cursor.getInt(cursor.getColumnIndex(ID)));
            userNotification.setDomain_id(cursor.getInt(cursor.getColumnIndex(DOMAIN_ID)));
            userNotification.setLevel(cursor.getInt(cursor.getColumnIndex(LEVEL)));
            userNotification.setNotification(cursor.getString(cursor.getColumnIndex(NOTIFICATION)));
            userNotification.setUrl(cursor.getString(cursor.getColumnIndex(RESOURCE_URL)));
            userNotification.setVideo(cursor.getString(cursor.getColumnIndex(VIDEO)));
        }
        cursor.close();
        closeDB();
        return userNotification;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public ArrayList<UserNotification> getDomainNotifications(int domain_id, int level) {
        ArrayList<UserNotification> userNotifications = new ArrayList<>();
        openDB();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS +
                " WHERE domain_id = " + domain_id + " AND level = " + level, null);
        while (cursor.moveToNext()) {
            UserNotification userNotification = new UserNotification();
            userNotification.setId(cursor.getInt(cursor.getColumnIndex(ID)));
            userNotification.setNotification(cursor.getString(cursor.getColumnIndex(NOTIFICATION)));
            userNotification.setDomain_id(cursor.getInt(cursor.getColumnIndex(DOMAIN_ID)));
            userNotification.setLevel(cursor.getInt(cursor.getColumnIndex(LEVEL)));
            userNotification.setUrl(cursor.getString(cursor.getColumnIndex(RESOURCE_URL)));
            userNotification.setVideo(cursor.getString(cursor.getColumnIndex(VIDEO)));
            userNotifications.add(userNotification);
        }
        closeDB();
        return userNotifications;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public ArrayList<UserNotification> getAllNotifications() {
        ArrayList<UserNotification> userNotifications = new ArrayList<>();
        openDB();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS, null);
        while (cursor.moveToNext()) {
            UserNotification userNotification = new UserNotification();
            userNotification.setId(cursor.getInt(cursor.getColumnIndex(ID)));
            userNotification.setNotification(cursor.getString(cursor.getColumnIndex(NOTIFICATION)));
            userNotification.setDomain_id(cursor.getInt(cursor.getColumnIndex(DOMAIN_ID)));
            userNotification.setLevel(cursor.getInt(cursor.getColumnIndex(LEVEL)));
            userNotification.setUrl(cursor.getString(cursor.getColumnIndex(RESOURCE_URL)));
            userNotification.setVideo(cursor.getString(cursor.getColumnIndex(VIDEO)));
            userNotifications.add(userNotification);
        }
        closeDB();
        return userNotifications;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // TABLE_QUESTIONNAIRE_RESULTS CRUD
    public QResult getResult(int id) {
        openDB();
        QResult qResult = new QResult();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_QUESTIONNAIRE_RESULTS + " where id = " + id, null);
        while (cursor.moveToNext()) {
            qResult = getQResultData(cursor);
        }
        cursor.close();
        closeDB();
        return qResult;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public QResult getLastResult() {
        openDB();
        QResult qResult = new QResult();
        Cursor cursor = db.rawQuery("SELECT * FROM  " + TABLE_QUESTIONNAIRE_RESULTS + " ORDER BY id DESC LIMIT 1", null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                qResult = getQResultData(cursor);
                cursor.close();
                closeDB();
            }
        }else {
            closeDB();
            qResult =  null;
        }
        return qResult;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public ArrayList<QResult> getAllResults() {
        ArrayList<QResult> qResults = new ArrayList<>();
        openDB();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_QUESTIONNAIRE_RESULTS, null);
        while (cursor.moveToNext()) {
            QResult qResult = getQResultData(cursor);
            qResults.add(qResult);
        }
        closeDB();
        return qResults;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean createResult(QResult qResult) {
        long result = -1;
        openDB();
        ContentValues values = new ContentValues();
        values.put(DOMAIN_ID, qResult.getDomain_id());
        values.put(RESULT, qResult.getResult());
        values.put(LEVEL, qResult.getLevel());
        values.put(TIME_STAMP, getCurrentTimeStamp());
        try {
            result = db.insert(TABLE_QUESTIONNAIRE_RESULTS, null, values);

        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
        closeDB();
        return result != -1;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean updateResult(QResult qResult) {
        long result = 0;
        openDB();
        ContentValues values = new ContentValues();
        values.put(DOMAIN_ID, qResult.getDomain_id());
        values.put(RESULT, qResult.getResult());
        values.put(LEVEL, qResult.getLevel());
        try {
            result = db.update(TABLE_QUESTIONNAIRE_RESULTS, values, ID + "=" + qResult.getId(), null);
        } catch (Exception e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        closeDB();
        return result != 0;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean deleteResult(int id) {
        long result = 0;
        openDB();
        try {
            result = db.delete(TABLE_QUESTIONNAIRE_RESULTS, ID + "=" + id, null);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
        closeDB();
        return result != 0;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public QResult getQResultData(Cursor cursor){
        QResult qResult = new QResult();
        qResult.setId(cursor.getInt(cursor.getColumnIndex(ID)));
        qResult.setDomain_id(cursor.getInt(cursor.getColumnIndex(DOMAIN_ID)));
        qResult.setLevel(cursor.getInt(cursor.getColumnIndex(LEVEL)));
        qResult.setResult(cursor.getInt(cursor.getColumnIndex(RESULT)));
        qResult.setTimestamp(cursor.getString(cursor.getColumnIndex(TIME_STAMP)));
        return qResult;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static String getCurrentTimeStamp(){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTimeStamp = dateFormat.format(new Date()); // Find todays date
            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // TABLE_CHAT_GROUPS CRUD
    public ChatGroup getGroup(int id) {
        openDB();
        ChatGroup chatGroup = new ChatGroup();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CHAT_GROUPS + " where id = " + id, null);
        while (cursor.moveToNext()) {
            chatGroup.setId(cursor.getInt(cursor.getColumnIndex(ID)));
            chatGroup.setName(cursor.getString(cursor.getColumnIndex(NAME)));
            chatGroup.setImage(cursor.getString(cursor.getColumnIndex(IMAGE)));
            chatGroup.setEnrolled(cursor.getInt(cursor.getColumnIndex(ENROLLED)));
        }
        cursor.close();
        closeDB();
        return chatGroup;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public ArrayList<ChatGroup> getAllGroups() {
        ArrayList<ChatGroup> chatGroups = new ArrayList<>();
        openDB();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CHAT_GROUPS, null);
        while (cursor.moveToNext()) {
            ChatGroup chatGroup = new ChatGroup();
            chatGroup.setId(cursor.getInt(cursor.getColumnIndex(ID)));
            chatGroup.setName(cursor.getString(cursor.getColumnIndex(NAME)));
            chatGroup.setImage(cursor.getString(cursor.getColumnIndex(IMAGE)));
            chatGroup.setEnrolled(cursor.getInt(cursor.getColumnIndex(ENROLLED)));
            chatGroups.add(chatGroup);
        }
        closeDB();
        return chatGroups;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean updateGroup(ChatGroup chatGroup) {
        long result = 0;
        openDB();
        ContentValues values = new ContentValues();
        values.put(NAME, chatGroup.getName());
        values.put(IMAGE, chatGroup.getImage());
        values.put(ENROLLED, chatGroup.getEnrolled());
        try {
            result = db.update(TABLE_CHAT_GROUPS, values, ID + "=" + chatGroup.getId(), null);
        } catch (Exception e) {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
        closeDB();
        return result != 0;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}