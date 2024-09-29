package com.example.prm_mini_project.Entity;
import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    // Constructor
    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Method to store login session
    public void createLoginSession(String username) {
        editor.putString(KEY_USERNAME, username);   // Store username
        editor.putBoolean(KEY_IS_LOGGED_IN, true);  // Set login status to true
        editor.commit();                            // Save changes
    }

    // Method to check login status
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);  // Default is false
    }

    // Get stored session username
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);  // Default is null
    }

    // Logout method to clear session
    public void logoutUser() {
        editor.clear();
        editor.commit();  // Clear all session data
    }
}

