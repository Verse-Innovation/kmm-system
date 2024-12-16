@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.verse.system

import io.tagd.arch.datatype.DataObject
import io.tagd.arch.infra.CompressedResource
import io.tagd.langx.Context
import io.tagd.langx.Locale

expect class Device(context: Context?) : DataObject {

    val os: OperatingSystem

    val firmware: Firmware

    val display: Display

    val userAgent: String?
}


expect class OperatingSystem() : DataObject {
    val name: String?
    val version: String?
    val versionName: String?
    val defaultLocale: Locale?
    val apiLevel: Int

    fun themeLabel(context: Context): String

    companion object {
        val DARK_THEME: String
        val LIGHT_THEME: String
    }
}

expect class Firmware() : DataObject {

    val model: String?

    val manufacturer: String?

    fun canTelephone(context: Context): Boolean

    fun category(context: Context): String

    fun hasNotch(context: Context): Boolean

    /**
     * takes in an array of Pair<Manufacturer, Model> that supports notch and returns whether
     * the current device is one of them
     */
    fun hasNotch(manufacturerAndModels: Array<Pair<String, String>>): Boolean


    fun inManufactures(list: List<String>): Boolean

    fun inModels(list: List<String>): Boolean

    companion object {

        val TAB: String

        val MOBILE: String
    }
}

expect class Display(context: Context?) : DataObject {

    val density: Float
    val scaledDensity: Float
    val widthDp: Int
    val heightDp: Int
    val widthPx: Int
    val heightPx: Int
    val widthScaleFactor: Float

    fun toPx(dimension: CompressedResource): Float

    fun toDp(px: Float): Float

    fun toPx(dp: Float): Float

    fun toDp(px: Int): Int

    fun toPx(dp: Int): Int

    fun sp2px(sp: Float): Float
}