package com.kurnia.ticket_wosh_app.api

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    companion object {
        private const val PREF_NAME = "wosh_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_EMAIL = "email"
        private const val KEY_PHONE = "phone"
        private const val KEY_SERVER_IP = "server_ip"
    }

    fun saveSession(userId: Int, fullName: String, email: String, phone: String) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putInt(KEY_USER_ID, userId)
        editor.putString(KEY_FULL_NAME, fullName)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_PHONE, phone)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, 1) // Default to 1 if not found
    }

    fun getFullName(): String? {
        return prefs.getString(KEY_FULL_NAME, "")
    }

    fun getEmail(): String? {
        return prefs.getString(KEY_EMAIL, "")
    }

    fun getPhone(): String? {
        return prefs.getString(KEY_PHONE, "")
    }

    fun saveServerIp(ip: String) {
        editor.putString(KEY_SERVER_IP, ip)
        editor.apply()
        RetrofitClient.updateIpAddress(ip)
    }

    fun getServerIp(): String {
        return RetrofitClient.IP_LAPTOP_KAMU
    }

    fun logout() {
        editor.clear()
        editor.apply()
    }
}
