package com.jumpa.jchat.pref

import android.content.Context

internal class SharedPref(context: Context) {
    companion object {
        private const val PREFS_NAME = "jchat_pref"
        private const val IS_LOGIN = "is_login"

    }

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val editor = preferences.edit()

    fun setLoggin(isLogin: Boolean) {
        editor?.putBoolean(IS_LOGIN, isLogin)
        editor?.commit()
    }

    fun isLogin(): Boolean {
        return preferences.getBoolean(IS_LOGIN, false)
    }
    fun removeData() {
        editor?.clear()
        editor?.commit()
    }
}