package com.ls.dailytaskplanner.utils

object Constants {
    const val EXPIRED = "res_expired"
    val HEX_LOWERCASE =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
    object SharedPrefKey {
        const val KEY_AGE = "age"
        const val KEY_DID_CONGRATULATION = "did_congratulation"
        const val KEY_ENABLE_SOUND_NOTIFY = "enable_sound_notify"
        const val KEY_LAST_TIME_INVITE_CREATE_PLAN = "last_time_invite_create_plan"
        const val KEY_REMIND_TASK_BEFORE = "remind_task_before"
        const val KEY_REMIND_CREATE_PLAN = "remind_create_plan"
        const val KEY_ENABLE_NOTIFY = "enable_notify"
        const val KEY_LAST_TIME_NOTIFY_UPDATE_TASK = "last_time_notify_update_task"
        const val KEY_CAN_SHOW_OPEN_ADS = "can_show_open_ads"
        const val KEY_DID_CHOOSE_LANGUAGE = "did_choose_language"
        const val KEY_OPEN_COUNT = "open_count"
    }

    object IntentKey {
        const val TYPE_NOTIFY = "type_notify"
    }
}