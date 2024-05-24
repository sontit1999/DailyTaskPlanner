package com.example.dailytaskplanner.model

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
    var waitingShowInter: Int = 30

    @SerializedName(value = "waitingShowReward")
    var waitingShowReward: Int = 1

    @SerializedName(value = "waitingShowOpenAds")
    var waitingShowOpenAds: Int = 1

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

    @SerializedName(value = "openAdKey")
    var openAdKey: String = "ca-app-pub-4945756407745123/8802590615"

    @SerializedName(value = "bannerAdKey")
    var bannerAdKey: String = "ca-app-pub-4945756407745123/1022660170"

    @SerializedName(value = "interAdKey")
    var interAdKey: String = "ca-app-pub-4945756407745123/3952507047"

    @SerializedName(value = "nativeAdKey")
    var nativeAdKey: String = "ca-app-pub-4945756407745123/4041218524"

    @SerializedName(value = "rewardAdKey")
    var rewardAdKey: String = "ca-app-pub-4945756407745123/2639425375"
    
    @SerializedName(value = "bannerAdKeyApplovin")
    var bannerAdKeyApplovin: String = "7e9d282524e92738"
    
    @SerializedName(value = "interAdKeyApplovin")
    var interAdKeyApplovin: String = "be5936464b11788f"
    
    @SerializedName(value = "nativeAdKeyApplovin")
    var nativeAdKeyApplovin: String = "fcc69dbe4d746e07"
    
    @SerializedName(value = "rewardAdKeyApplovin")
    var rewardAdKeyApplovin: String = "f3f43a8f1b671292"
    
    @SerializedName(value = "numberOfNativeDisplay")
    var numberOfNativeDisplay: Long = 4
    
    @SerializedName(value = "posAddNativeStart")
    var posAddNativeStart: Int = 2
    
    @SerializedName(value = "distanceNativeAd")
    var distanceNativeAd: Int = 7

}