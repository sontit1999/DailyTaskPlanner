package com.ls.dailytaskplanner.utils

object AllEvents {

    // inter
    const val INTER_SPLASH_LOAD_SUCCESS = "inter_splash_load_success"
    const val INTER_SPLASH_LOAD_FAIL = "inter_splash_load_fail"
    const val INTER_SPLASH_SHOW_SUCCESS = "inter_splash_show_success"
    const val INTER_SPLASH_SHOW_FAIL = "inter_splash_show_fail"
    const val INTER_SPLASH_SHOW_FAIL_NO_ADS = "inter_splash_show_fail_no_ads"

    const val INTER_LOAD_SUCCESS = "inter_load_success"
    const val INTER_LOAD_FAIL = "inter_load_fail"
    const val INTER_SHOW_SUCCESS = "inter_show_success"
    const val INTER_SHOW_FAIL = "inter_show_fail"
    const val INTER_CLICKED = "inter_click"
    const val INTER_SHOW_FAIL_NO_ADS = "inter_show_fail_no_ads"

    // native
    const val NATIVE_ADD_TASK_CLICK = "native_add_task_click"
    const val NATIVE_ADD_TASK_LOAD_SUCCESS = "native_add_task_load_success"
    const val NATIVE_ADD_TASK_LOAD_FAIL = "native_add_task_load_fail"
    const val NATIVE_ADD_TASK_IMPRESSION = "native_add_task_impression"

    const val NATIVE_AGE_CLICK = "native_age_click"
    const val NATIVE_AGE_LOAD_SUCCESS = "native_age_load_success"
    const val NATIVE_AGE_IMPRESSION = "native_age_impression"
    const val NATIVE_AGE_LOAD_FAIL = "native_age_load_fail"

    // banner
    const val BANNER_LOAD_SUCCESS = "banner_load_success_"
    const val BANNER_LOAD_FAIL = "banner_load_fail_"
    const val BANNER_CLICK = "banner_click_"


    // open ads
    const val OPEN_ADS_SPLASH_LOAD_SUCCESS = "open_splash_load_success"
    const val OPEN_ADS_SPLASH_LOAD_FAIL = "open_splash_load_fail"
    const val OPEN_ADS_SPLASH_SHOW_SUCCESS = "open_splash_show_success"
    const val OPEN_ADS_SPLASH_SHOW_FAIL = "open_splash_show_fail"
    const val OPEN_ADS_SPLASH_SHOW_FAIL_NO_ADS = "open_splash_show_fail_no_ads"
    const val OPEN_ADS_SPLASH_CLICK = "open_splash_click"

    const val OPEN_ADS_LOAD_SUCCESS = "open_ads_load_success"
    const val OPEN_ADS_LOAD_FAIL = "open_ads_load_fail"
    const val OPEN_ADS_SHOW_SUCCESS = "open_ads_show_success"
    const val OPEN_ADS_SHOW_FAIL = "open_ads_show_fail"
    const val OPEN_ADS_CLICKED = "open_ads_click"
    const val OPEN_ADS_SHOW_FAIL_NO_ADS = "open_ads_show_fail_no_ads"

    // other
    const val USER_FIRST_OPEN = "user_first_open"
    const val USER_REOPEN = "user_reopen"
    const val CONFIG_LOAD_SUCCESS = "config_load_success"
    const val CONFIG_LOAD_FAIL = "config_load_fail"
    const val CLICK_UPDATE_APP = "click_update_app"
    const val LOST_INTERNET = "internet_lost"
    const val AVAILABLE_INTERNET = "internet_available"

    // action user
    const val ACTION_CHOOSE_DATE = "action_choose_date"
    const val ACTION_ADD_TASK = "action_add_task"
    const val ACTION_CANCEL_ADD_TASK = "action_cancel_add_task"
    const val ACTION_CHOOSE_COLOR = "action_choose_color"
    const val ACTION_CHOOSE_DATE_TASK = "action_choose_date_task"
    const val ACTION_CHOOSE_TIME = "action_choose_time_task"
    const val ACTION_CHANGE_REMIND_TASK = "action_change_remind_task"
    const val ACTION_CHOOSE_LANGUAGE = "action_choose_language"
    const val ACTION_CHOOSE_TODAY = "action_choose_today"
    const val ACTION_CHANGE_NOTIFY_OFFLINE = "action_change_notify_offline"
    const val ACTION_CHANGE_NOTIFY_SOUND = "action_change_notify_sound"
    const val ACTION_CHANGE_TIME_REMIND_NEW_PLAN = "action_change_time_remind_new_plan"
    const val ACTION_CHANGE_TIME_REMIND_TASK = "action_change_time_remind_task"
    const val ACTION_CHANGE_STATUS_TASK = "action_change_status_task"


    // view
    const val VIEW_LANGUAGE = "view_language"
    const val VIEW_HOME = "view_home"
    const val VIEW_PROFILE = "view_profile"
    const val VIEW_ADD_TASK = "view_add_task"

    // service
    const val SERVICE_ON_CREATE = "service_create"
    const val SERVICE_ON_START_COMMAND = "service_start_command"
    const val SERVICE_DESTROY = "service_destroy"
    const val SERVICE_REMOVE_TASK = "service_remove_task"
    const val SERVICE_RESTART_AFTER_BOOST = "service_restart_after_boost"

    // NOTIFY
    const val NOTIFY_UPDATE_STATUS_TASK = "notify_status_task_"
    const val NOTIFY_INVITE_CREATE_PLAN = "notify_invite_create_plan_"
    const val NOTIFY_REMIND_TASK = "notify_remind_task_"
    const val NOTIFY_SATURDAY = "notify_saturday_"
    const val NOTIFY_DAILY = "notify_daily_"

    // TASK
    const val TASK_CREATE = "task_create"
    const val TASK_UPDATE = "task_update"
    const val TASK_REMOVE = "task_remove"

    // PERMISSION NOTIFY
    const val ACCEPT_PERMISSION_NOTIFY = "accept_permission_notify"
    const val DECLINE_PERMISSION_NOTIFY = "decline_permission_notify"
    const val OPEN_SETTING_NOTIFY = "open_setting_notify"
}