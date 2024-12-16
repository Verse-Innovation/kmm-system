@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.verse.system

import io.tagd.arch.datatype.DataObject
import io.tagd.arch.infra.CompressedResource
import io.tagd.core.ValidateException
import io.tagd.langx.Context
import io.tagd.langx.Locale
import io.tagd.langx.isNull
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.free
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import platform.Foundation.valueForKey
import platform.UIKit.UIDevice
import platform.WebKit.WKWebView
import platform.posix.uname
import platform.posix.utsname

actual class Device actual constructor(context: Context?) : DataObject() {

    actual val os: OperatingSystem
        get() = OperatingSystem()

    actual val firmware: Firmware
        get() = Firmware()

    actual val display: Display = Display(context)

    actual val userAgent: String? = WKWebView().valueForKey("userAgent")?.toString()

    override fun validate() {
        os.validate()
        firmware.validate()
        if (userAgent.isNullOrEmpty()) {
            throw ValidateException(this, "userAgent cannot be null or empty")
        }
    }


}

actual class OperatingSystem actual constructor() : DataObject() {

    actual val name: String? = UIDevice.currentDevice.systemName

    actual val version: String? = UIDevice.currentDevice.systemVersion

    actual val versionName: String? = ""

    actual val defaultLocale: Locale? = Locale()

    actual val apiLevel = 0

    actual fun themeLabel(context: Context): String {
        TODO("Not yet implemented")
    }

    override fun validate() {
        if (name.isNullOrEmpty()) {
            throw ValidateException(this, "name cannot be empty")
        }
        if (version.isNullOrEmpty()) {
            throw ValidateException(this, "version cannot be empty")
        }
        if (versionName.isNull()) {
            throw ValidateException(this, "versionName cannot be null")
        }
        if (defaultLocale.isNull()) {
            throw ValidateException(this, "defaultLocale cannot be null")
        }
        if (apiLevel.isNull()) {
            throw ValidateException(this, "apiLevel cannot be null")
        }
    }

    actual companion object {
        actual const val LIGHT_THEME = "light"
        actual const val DARK_THEME = "dark"
    }
}

actual class Firmware actual constructor() : DataObject() {

    actual val model: String? = getDeviceModelNumber()

    actual val manufacturer: String? = "Apple"

    @OptIn(ExperimentalForeignApi::class)
    private fun getDeviceModelNumber(): String {
        val systemInfo = nativeHeap.alloc<utsname>()
        uname(systemInfo.ptr)
        val machine = systemInfo.machine.toKString()
        nativeHeap.free(systemInfo.ptr)
        return machine
    }

    actual fun canTelephone(context: Context): Boolean {
        TODO("Not yet implemented")
    }

    actual fun category(context: Context): String {
        TODO("Not yet implemented")
    }

    /**
     * takes in an array of Pair<Manufacturer, Model> that supports notch and returns whether
     * the current device is one of them
     */
    actual fun hasNotch(manufacturerAndModels: Array<Pair<String, String>>): Boolean {
        TODO("Not yet implemented")
    }

    actual fun inManufactures(list: List<String>): Boolean {
        return list.contains(manufacturer)
    }

    actual fun inModels(list: List<String>): Boolean {
        return list.contains(model)
    }

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

    actual fun hasNotch(context: Context): Boolean {
        TODO("Not yet implemented")
    }
}

actual class Display actual constructor(context: Context?) :
    DataObject() {
    actual val density: Float
        get() = TODO("Not yet implemented")
    actual val scaledDensity: Float
        get() = TODO("Not yet implemented")
    actual val widthDp: Int
        get() = TODO("Not yet implemented")
    actual val heightDp: Int
        get() = TODO("Not yet implemented")
    actual val widthPx: Int
        get() = TODO("Not yet implemented")
    actual val heightPx: Int
        get() = TODO("Not yet implemented")
    actual val widthScaleFactor: Float
        get() = TODO("Not yet implemented")

    actual fun toPx(dimension: CompressedResource): Float {
        TODO("Not yet implemented")
    }

    actual fun toDp(px: Float): Float {
        TODO("Not yet implemented")
    }

    actual fun toPx(dp: Float): Float {
        TODO("Not yet implemented")
    }

    actual fun toDp(px: Int): Int {
        TODO("Not yet implemented")
    }

    actual fun toPx(dp: Int): Int {
        TODO("Not yet implemented")
    }

    actual fun sp2px(sp: Float): Float {
        TODO("Not yet implemented")
    }

}