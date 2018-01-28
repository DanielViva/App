package com.example.vivad.app.business

import android.content.Context
import android.content.SharedPreferences
import java.nio.channels.spi.AbstractSelectionKey

class UserBusiness (context: Context) {

    private val mSharedPreferences: SharedPreferences = context.getSharedPreferences("tasks",Context.MODE_PRIVATE)

    fun storeString (key: String, value: String){

        mSharedPreferences.edit().putString(key, value).apply()

    }
    fun getStoredString (key: String): String {

         return mSharedPreferences.getString(key, "")

    }

    fun removeStoredString(key: String) {
        mSharedPreferences.edit().remove(key).apply()

    }

    fun saveUser(email: String, password: String) {
        storeString("UserEmail", email)
        storeString("UserPassword", password)
    }

    fun removeUser(){
        removeStoredString("UserEmail")
        removeStoredString("UserPassword")
    }

}