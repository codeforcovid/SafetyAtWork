package com.safeatwork.common;

import android.content.Context;
import android.content.SharedPreferences;

import static com.safeatwork.model.Constants.APP_PREFERENCE;
import static com.safeatwork.model.Constants.PREF_ADMIN_ACCESS;
import static com.safeatwork.model.Constants.PREF_UNIQ_NOTIFY_ID;
import static com.safeatwork.model.Constants.PREF_USER_NAME;
import static com.safeatwork.model.Constants.PREF_USER_LOGIN;

public class PrefSession {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public PrefSession(Context context) {
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(APP_PREFERENCE, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setUserEmailLoginSession(String user_email) {
        editor.putString(PREF_USER_LOGIN, user_email);
        editor.apply();
    }

    public void setUserName(String name) {
        editor.putString(PREF_USER_NAME, name);
        editor.apply();
    }
    public void setAdminAccess(String admin_access) {
        editor.putString(PREF_ADMIN_ACCESS, admin_access);
        editor.apply();
    }

    public int getUniqueNotifyId() {
        int value = pref.getInt(PREF_UNIQ_NOTIFY_ID, 0) + 1;
        editor.putInt(PREF_UNIQ_NOTIFY_ID, value);
        editor.apply();
        return value;
    }

    public String getUserEmailLoginSession() {
        return pref.getString(PREF_USER_LOGIN, "0");
    }
    public String getUserName() {
        return pref.getString(PREF_USER_NAME, "0");
    }
    public String getAdminAccess() {
        return pref.getString(PREF_ADMIN_ACCESS, "0");
    }
}