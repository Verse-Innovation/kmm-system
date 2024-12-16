@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.verse.system

import android.app.Activity
import android.content.res.Configuration
import android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE
import android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK
import android.content.res.Configuration.SCREENLAYOUT_SIZE_XLARGE
import android.os.Build
import android.os.Build.VERSION_CODES
import android.telephony.TelephonyManager
import android.view.DisplayCutout
import android.view.WindowInsets
import io.tagd.arch.control.IApplication
import io.tagd.arch.datatype.DataObject
import io.tagd.arch.infra.CompressedResource
import io.tagd.core.ValidateException
import io.tagd.core.annotation.VisibleForTesting
import io.tagd.langx.Context
import io.tagd.langx.Locale
import io.tagd.langx.isNull
import io.tagd.langx.ref.WeakReference
import io.tagd.langx.ref.weak

actual class Device actual constructor(context: Context?) : DataObject() {

    actual val os: OperatingSystem = OperatingSystem()

    actual val firmware: Firmware = Firmware()

    actual val userAgent: String? = resolveUserAgent()

    actual val display: Display = Display(context)

    private fun resolveUserAgent() = System.getProperty("http.agent")

    override fun validate() {
        os.validate()
        firmware.validate()
        display.validate()

        if (userAgent.isNullOrEmpty()) {
            throw ValidateException(this, "userAgent cannot be null or empty")
        }
    }
}

actual class OperatingSystem actual constructor() : DataObject() {

    actual val name: String? = "Android"

    actual val version: String? = resolveVersion()

    @androidx.annotation.ChecksSdkIntAtLeast(extension = 0)
    actual val apiLevel: Int = resolveApiLevel()

    actual val defaultLocale: Locale? = Locale.default()

    actual val versionName: String? = resolveOsVersionName(apiLevel)

    actual fun themeLabel(context: Context): String {
        return if (isNightMode(context)) {
            DARK_THEME
        } else {
            LIGHT_THEME
        }
    }

    private fun isNightMode(context: Context): Boolean {
        val nightModeFlag = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlag == Configuration.UI_MODE_NIGHT_YES
    }

    private fun resolveVersion() = Build.VERSION.RELEASE

    private fun resolveApiLevel() = Build.VERSION.SDK_INT

    private fun resolveOsVersionName(apiLevel: Int): String {
        val fields = VERSION_CODES::class.java.fields
        return fields.firstOrNull {
            val intValue = it.getInt(VERSION_CODES::class)
            intValue == apiLevel
        }?.name ?: "UNKNOWN"
    }

    override fun validate() {
        if (name.isNullOrEmpty()) {
            throw ValidateException(this, "name cannot be empty")
        }
        if (version.isNullOrEmpty()) {
            throw ValidateException(this, "version cannot be empty")
        }
        if (versionName.isNullOrEmpty() || versionName == "UNKNOWN") {
            throw ValidateException(this, "versionName cannot be null or empty or unknown")
        }
        if (defaultLocale.isNull()) {
            throw ValidateException(this, "defaultLocale cannot be null")
        }
        if (apiLevel.isNull() || apiLevel == 0) {
            throw ValidateException(this, "invalid apiLevel")
        }
    }

    actual companion object {
        actual const val LIGHT_THEME = "light"
        actual const val DARK_THEME = "dark"
    }
}

actual class Firmware actual constructor() : DataObject() {

    actual val model: String? = Build.MODEL

    actual val manufacturer: String? = Build.MANUFACTURER

    @VisibleForTesting
    internal var canTelephone: Boolean? = null

    @VisibleForTesting
    internal var category: String? = null

    @VisibleForTesting
    internal var hasNotch: Boolean? = null

    actual fun canTelephone(context: Context): Boolean {
        return canTelephone ?: kotlin.run {
            try {
                val telephonyServiceName = android.content.Context.TELEPHONY_SERVICE
                val tm = context.getSystemService(telephonyServiceName) as TelephonyManager
                val canTelephone = tm.phoneType != TelephonyManager.PHONE_TYPE_NONE
                canTelephone
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }.also {
                canTelephone = it
            }
        }
    }

    actual fun category(context: Context): String {
        return category ?: kotlin.run {
            try {
                val screenLayout = context.resources.configuration.screenLayout
                val xlarge = screenLayout and SCREENLAYOUT_SIZE_MASK == SCREENLAYOUT_SIZE_XLARGE
                val large = screenLayout and SCREENLAYOUT_SIZE_MASK == SCREENLAYOUT_SIZE_LARGE
                if (xlarge || large) TAB else MOBILE
            } catch (e: Exception) {
                e.printStackTrace()
                MOBILE
            }.also {
                category = it
            }
        }
    }

    actual fun hasNotch(context: Context): Boolean {
        var isNotch = false
        var displayCutout: DisplayCutout? = null
        if (Build.VERSION.SDK_INT >= VERSION_CODES.P) {
            var windowInsets: WindowInsets? = null
            if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
                ((context.applicationContext as? IApplication)?.currentView() as? Activity)?.let {
                    windowInsets = it.window.decorView.rootWindowInsets
                }
            }
            if (windowInsets != null) {
                if (Build.VERSION.SDK_INT >= 28) {
                    displayCutout = windowInsets?.displayCutout
                    if (displayCutout != null) {
                        displayCutout.safeInsetTop
                        isNotch = true
                    }
                }
            }
        } else {
            isNotch = hasNotch ?: false
        }
        return isNotch
    }

    /**
     * takes in an array of Pair<Manufacturer, Model> that supports notch and returns whether
     * the current device is one of them
     */
    actual fun hasNotch(manufacturerAndModels: Array<Pair<String, String>>): Boolean {
        return hasNotch ?: kotlin.run {
            val hasNotch = hasNotchInternal(manufacturerAndModels)
            hasNotch.also {
                this.hasNotch = it
            }
        }
    }

    private fun hasNotchInternal(manufacturerAndModels: Array<Pair<String, String>>) =
        manufacturer?.takeIf { it.isNotEmpty() }?.let { currentDeviceManufacturer ->
            model?.takeIf { it.isNotEmpty() }?.let { currentDeviceModel ->

                manufacturerAndModels.any { (manufacturer, model) ->
                    currentDeviceManufacturer.startsWith(manufacturer, ignoreCase = true)
                            && currentDeviceModel.startsWith(model, ignoreCase = true)
                }
            }
        } ?: false

    actual fun inManufactures(list: List<String>): Boolean {
        return list.any { it.equals(manufacturer, ignoreCase = true) }
    }

    actual fun inModels(list: List<String>): Boolean {
        return list.any { it.equals(model, ignoreCase = true) }
    }

    @Suppress("warnings")
    override fun validate() {
        if (model.isNullOrEmpty()) {
            throw ValidateException(this, "model cannot be empty")
        }
        if (manufacturer.isNullOrEmpty()) {
            throw ValidateException(this, "manufacturer cannot be empty")
        }
    }


    actual companion object {

        actual val MOBILE = "mobile"
        actual val TAB: String = "tab"
    }
}

actual class Display actual constructor(context: Context?): DataObject() {

    private var weakContext: WeakReference<Context>? = context?.weak()

    private val metrics
        get() = weakContext?.get()?.resources?.displayMetrics!!

    override fun validate() {
        if (widthDp == 0 || heightDp == 0 || density == 0f) {
            throw ValidateException(this, "metrics failed to load")
        }
    }

    actual val density: Float  by lazy {
        metrics.density
    }

    actual val scaledDensity: Float  by lazy {
        metrics.scaledDensity
    }

    actual val widthDp: Int by lazy {
        (metrics.widthPixels / density).toInt()
    }

    actual val heightDp: Int by lazy {
        (metrics.heightPixels / density).toInt()
    }

    actual val widthPx: Int by lazy {
        metrics.widthPixels
    }

    actual val heightPx: Int by lazy {
        metrics.heightPixels
    }

    actual val widthScaleFactor: Float by lazy {
        val baseScreenWidthInDp = 320f
        widthDp / baseScreenWidthInDp
    }

    actual fun toDp(px: Float): Float {
        return px / density
    }

    actual fun toPx(dimension: CompressedResource): Float {
        val dp = weakContext?.get()?.resources?.getDimension(dimension.identifier)!!
        return toPx(dp)
    }

    actual fun toPx(dp: Float): Float {
        return dp * density
    }

    actual fun toDp(px: Int): Int {
        return (px / density).toInt()
    }

    actual fun toPx(dp: Int): Int {
        return (dp * density).toInt()
    }

    actual fun sp2px(sp: Float): Float {
        return scaledDensity * sp
    }
}