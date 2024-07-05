package com.ls.dailytaskplanner.model

import com.google.gson.annotations.SerializedName

class CommonConfig {

    @SerializedName(value = "isActiveAds")
    var isActiveAds: Boolean = false

    @SerializedName(value = "supportInter")
    var supportInter: Boolean = false

    @SerializedName(value = "supportNative")
    var supportNative: Boolean = false

    @SerializedName(value = "supportReward")
    var supportReward: Boolean = false

    @SerializedName(value = "supportBanner")
    var supportBanner: Boolean = false

    @SerializedName(value = "supportOpenAds")
    var supportOpenAds: Boolean = false

    @SerializedName(value = "waitingShowInter")
    var waitingShowInter: Int = 40

    @SerializedName(value = "latestVersion")
    var latestVersion: String = ""

    @SerializedName(value = "packageName")
    var packageName: String = ""

    @SerializedName(value = "timeCheckTask")
    var timeCheckTask: Int = 600

    @SerializedName(value = "versionCodeForReview")
    var versionCodeForReview: Int = 0

    @SerializedName(value = "openAdSplashKey")
    var openAdSplashKey: String = "ca-app-pub-4945756407745123/8715417519"

    @SerializedName(value = "openAdKey")
    var openAdKey: String = "ca-app-pub-4945756407745123/3154300775"

    @SerializedName(value = "bannerAdKey")
    var bannerAdKey: String = "ca-app-pub-4945756407745123/4466881433"

    @SerializedName(value = "interSlashAdKey")
    var interSplashAdKey = "ca-app-pub-4945756407745123/8370768740"

    @SerializedName(value = "interAdKey")
    var interAdKey: String = "ca-app-pub-4945756407745123/7057687070"

    @SerializedName(value = "nativeListAdKey")
    var nativeListAdKey: String = "ca-app-pub-4945756407745123/3901040699"

    @SerializedName(value = "nativeAgeKey")
    var nativeAgeKey: String = "ca-app-pub-4945756407745123/6375661806"

    @SerializedName(value = "rewardAdKey")
    var rewardAdKey: String = "ca-app-pub-4945756407745123/5044685688"

    fun resetAd(){
        isActiveAds = false
        supportInter = false
        supportNative = false
        supportReward = false
        supportBanner = false
        supportOpenAds = false
    }

}