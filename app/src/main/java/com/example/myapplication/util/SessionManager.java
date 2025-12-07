package com.example.myapplication.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.myapplication.UI.Login.LoginActivity;

public class SessionManager {

    private static final String PREF_NAME = "UserSessionPref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ROLE = "userRole";
    private static final String KEY_USER_ID = "userId";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_USER = "user";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String userId, String role, boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.putString(KEY_USER_ROLE, role);
        editor.putString(KEY_USER_ID, userId);
        editor.commit(); 
    }

    public void checkLogin() {
        if (!this.isLoggedIn()) {
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public void logoutUser() {
        clearSession(); // Use the new clear method

        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }
    
    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserRole() {
        return pref.getString(KEY_USER_ROLE, ROLE_USER); // Default to user
    }

    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }
}
