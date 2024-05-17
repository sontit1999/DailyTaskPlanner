package com.example.dailytaskplanner.utils

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
    }
}