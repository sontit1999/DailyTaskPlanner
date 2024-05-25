package com.ls.dailytaskplanner.model

import com.google.gson.annotations.SerializedName

class CommonConfig {

    @SerializedName(value = "isActiveAds")
    var isActiveAds: Boolean = true

    @SerializedName(value = "supportInter")
    var supportInter: Boolean = true

    @SerializedName(value = "supportNative")
    var supportNative: Boolean = true

    @SerializedName(value = "supportReward")
    var supportReward: Boolean = true

    @SerializedName(value = "supportBanner")
    var supportBanner: Boolean = true

    @SerializedName(value = "supportOpenAds")
    var supportOpenAds: Boolean = true

    @SerializedName(value = "waitingShowInter")
    var waitingShowInter: Int = 40

    @SerializedName(value = "latestVersion")
    var latestVersion: String = ""

    @SerializedName(value = "packageName")
    var packageName: String = "com.hs.entertainment.hotgirlwallpaper"


    @SerializedName(value = "distanceNativeInList")
    var distanceNativeInList: Int = 6

    @SerializedName(value = "scenarioChangedWallpaper")
    var scenarioChangedWallpaper: String = "24,24,48,72,168" // days

    @SerializedName(value = "timeNotify10m")
    var timeNotify10m: Int = 10

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

    @SerializedName(value = "rewardAdKey")
    var rewardAdKey: String = "ca-app-pub-4945756407745123/5044685688"

    @SerializedName(value = "numberOfNativeDisplay")
    var numberOfNativeDisplay: Long = 4
    
    @SerializedName(value = "posAddNativeStart")
    var posAddNativeStart: Int = 2
    
    @SerializedName(value = "distanceNativeAd")
    var distanceNativeAd: Int = 7

}