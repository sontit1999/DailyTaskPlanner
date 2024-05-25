package com.ls.dailytaskplanner.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class ConfigModel {
    @SerializedName(value = "commonInfo")
    var commonInfo = CommonConfig()

    companion object {
        fun newInstance(json: String): ConfigModel {
            return try {
                Gson().fromJson(json, ConfigModel::class.java)
            } catch (e: Exception) {
                ConfigModel()
            }
        }
    }
}