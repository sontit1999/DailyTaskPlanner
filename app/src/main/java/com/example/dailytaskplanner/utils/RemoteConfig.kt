package com.example.dailytaskplanner.utils

import com.example.dailytaskplanner.model.ConfigModel

object RemoteConfig {
    private var configModel = ConfigModel()

    val commonConfig
        get() = configModel.commonInfo
}