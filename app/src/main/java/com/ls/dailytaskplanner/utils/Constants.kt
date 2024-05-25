package com.ls.dailytaskplanner.utils

object Constants {
    const val EXPIRED = "res_expired"
    val HEX_LOWERCASE =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
    const val FORMAT_DATE_TIME = "HH:mm dd/MM/yyyy"
    const val FORMAT_DATE = "dd/MM/yyyy"
    const val SEARCH_IGNORE = "[+^\\\\\"*&%$#@!~=;:<>/?.()]"
    const val EMOJIS_REGEX = "[^\\p{L}\\p{N}\\p{P}\\p{Z}]"
    const val BASE_URL = "https://dogonoithatxinh.com/minringtone/rest/"
    const val FILE_NAME_POLICY = "policy"
    const val FILE_NAME_RINGTONE = "ringtone.json"
    const val NEW_PACKAGE = "update_pkg_name_key"
    const val LATEST_VERSION_APP = "latest_version_app"
    const val SEX_MALE = "male"
    const val SEX_FEMALE = "female"
    const val KEY_AVATAR = 113
    const val ONE_MINUTE: Long = 1000 * 60
    const val TWELVE_HOUR = 12 * 3600 * 1_000L
    const val THIRTY_DAYS_IN_MILLIS = 30 * 24 * 60 * 60 * 1000L
    const val ONE_DAYS_IN_MILLIS = 24 * 60 * 60 * 1000L

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
    }
}