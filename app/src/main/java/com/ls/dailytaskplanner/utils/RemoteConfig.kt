package com.ls.dailytaskplanner.utils

import com.ls.dailytaskplanner.model.ConfigModel

object RemoteConfig {
    var configModel = ConfigModel()

    val commonConfig
        get() = configModel.commonInfo
}