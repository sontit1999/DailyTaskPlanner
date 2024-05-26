package com.ls.dailytaskplanner.database.storage

import kotlin.reflect.KClass

interface LocalStorage {

    fun putString(key: String, value: String?)
    fun getString(key: String): String?
    fun remove(key: String)

    var authorization: String?

    fun <T : Any> putData(key: String, t: T?)

    fun <T : Any> getData(key: String): T?

    fun <T : Any> getData(key: String, clazz: KClass<T>): T?

    var age: String

    var didCongratulate: Boolean

    var enableSoundNotify : Boolean

    var lastTimeInviteCreatePlan : Long

    var remindTaskBefore : String

    var remindCreatePlan : String

    var enableNotifyApp : Boolean

    var lastTimeNotifyUpdateStatusTask : Long

    var canShowOpenAd : Boolean

    var didChooseLanguage: Boolean
}