package com.example.dailytaskplanner.utils

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import com.example.dailytaskplanner.R
import kotlin.properties.Delegates

object AppConfig {

	const val COMPANY_NAME = "Ahaa"
	lateinit var connectivityManager: ConnectivityManager
	lateinit var displayMetrics: DisplayMetrics
	lateinit var appName: String
	private var statusBarSize by Delegates.notNull<Int>()

	val widthScreen: Int
		get() = displayMetrics.widthPixels

	val heightScreen: Int
		get() = displayMetrics.heightPixels

	var aspectRatio: Float = 1.78f

	private fun getAspectRatioIndex(ratio: Float): Int {
		return when {
			ratio <= 1.89f -> 0
			ratio > 1.89f && ratio <= 2.055f -> 1
			ratio > 2.055f && ratio <= 2.14f -> 2
			ratio > 2.14 && ratio <= 2.195f -> 3
			else -> 4
		}
	}

	fun setup(context: Context) {
		appName = context.getString(R.string.app_name)
		connectivityManager =
			context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		displayMetrics = getScreen(context)
		statusBarSize = getStatusBarHeight(context)
		aspectRatio = (heightScreen.toFloat() / widthScreen)
	}

	private fun getScreen(context: Context): DisplayMetrics {
		val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
		val dm = DisplayMetrics()
		windowManager.defaultDisplay.getRealMetrics(dm)
		return dm
	}

	private fun getStatusBarHeight(context: Context): Int {
		val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
		return if (resourceId > 0) {
			context.resources.getDimensionPixelSize(resourceId)
		} else 0
	}


	@JvmStatic
	fun isQ() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q


}

fun dpToPx(dp: Float): Int =
	TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, AppConfig.displayMetrics).toInt()

fun dp2Px(dp: Float): Float =
	TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, AppConfig.displayMetrics)

fun pxToDp(px: Int): Float =
	px / AppConfig.displayMetrics.density

fun spToPx(sp: Float): Float =
	TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, AppConfig.displayMetrics)