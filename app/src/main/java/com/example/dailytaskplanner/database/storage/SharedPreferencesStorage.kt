package com.example.dailytaskplanner.database.storage

import android.content.Context
import com.example.dailytaskplanner.utils.AES
import com.example.dailytaskplanner.utils.Constants
import com.example.dailytaskplanner.utils.toHex
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.reflect.KClass

class SharedPreferencesStorage @Inject constructor(
    @ApplicationContext context: Context,
) : LocalStorage {

    private val fileName = "daily_task_planner"
    private val sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    private val secret = fileName.toHex(fileName)

    override fun putString(key: String, value: String?) {
        with(sharedPreferences.edit()) {
            putString(key, value?.let { AES.enc(it, secret) })
            apply()
        }
    }

    override fun getString(key: String): String? {
        val str = sharedPreferences.getString(key, null) ?: return null
        return AES.dec(str, secret)
    }


    override fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    override var authorization: String?
        get() = getString("authorization")
        set(value) {
            putString("authorization", value)
        }

    override fun <T : Any> putData(key: String, t: T?) {
        if (t != null) {
            val str = Gson().toJson(t)
            putString(key, str)
        } else putString(key, null)
    }

    override fun <T : Any> getData(key: String): T? {
        val string = getString(key) ?: return null
        try {
            return Gson().fromJson(string, object : TypeToken<T>() {}.type)
        } catch (e: Exception) {
        }
        return null
    }

    override fun <T : Any> getData(key: String, clazz: KClass<T>): T? {
        val string = getString(key) ?: return null
        try {
            return Gson().fromJson(string, clazz.java)
        } catch (e: Exception) {
        }
        return null
    }

    override var age: String
        get() = getString(Constants.SharedPrefKey.KEY_AGE) ?: ""
        set(value) {
            putString(Constants.SharedPrefKey.KEY_AGE, value)
        }
    override var didCongratulate: Boolean
        get() = getData(Constants.SharedPrefKey.KEY_DID_CONGRATULATION, Boolean::class) ?: false
        set(value) {
            putData(Constants.SharedPrefKey.KEY_DID_CONGRATULATION, value)
        }

}