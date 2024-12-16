package io.verse.system

import android.content.Context.TELEPHONY_SERVICE
import android.content.res.Configuration
import android.os.Build
import android.telephony.TelephonyManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.tagd.langx.Context
import io.tagd.langx.assertNotNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class DeviceAndroidTest {

    private val device = Device(
        InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Context
    )

    @Test
    fun validate_shouldNotThrow_exception() {
        val validated = try {
            device.validate()
            true
        } catch (e: Exception) {
            false
        }

        assertTrue(validated)
    }

    @Test
    fun given_osVersion_thenVerify_itIs_BuildVERSION_RELEASE() {
        val os = device.os

        assertNotNull(Build.VERSION.RELEASE)
        assertTrue(os.version == Build.VERSION.RELEASE)
    }

    @Test
    fun given_osApiLevel_thenVerify_itIs_SDK_INT() {
        val os = device.os

        assertNotNull(Build.VERSION.SDK_INT)
        assertTrue(os.apiLevel == Build.VERSION.SDK_INT)
    }

    @Test
    fun canTelephone_shouldCache_itsValue() {
        val context = InstrumentationRegistry.getInstrumentation()
            .targetContext.applicationContext as Context

        val cachedValue = device.firmware.canTelephone(context)

        assertEquals(cachedValue, device.firmware.canTelephone)
    }

    @Test
    fun given_device_thenVerify_canTelephone_returnsExpected_fromTelephony() {
        // given
        val context =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Context
        val canTelephone = device.firmware.canTelephone(context)

        // expected
        val expectedCanTelephony = (context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager)
            .phoneType != TelephonyManager.PHONE_TYPE_NONE

        // verify
        println("CanTelephone: $canTelephone")
        assertEquals(expectedCanTelephony, canTelephone)
    }

    @Test
    fun category_shouldCache_itsValue() {
        val context = InstrumentationRegistry.getInstrumentation()
            .targetContext.applicationContext as Context

        val cachedValue = device.firmware.category(context)

        assertEquals(cachedValue, device.firmware.category)
    }

    @Test
    fun given_device_then_verifyCategory_asPhoneOrTab() {
        // given
        val context =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Context
        val category = device.firmware.category(context)

        // verify
        println("Category: $category")
    }

    @Test
    fun hasNotch_shouldCache_itsValue() {
        val notchDeviceManufacturers = arrayOf("pixel" to "3", "google" to "6", "lenevo" to "A52")
        val cachedValue = device.firmware.hasNotch(notchDeviceManufacturers)

        assertEquals(cachedValue, device.firmware.hasNotch)
    }

    @Test
    fun given_device_thenVerify_isProbableNotch_returnTrue_ifPresentInGivenNotchDevicesList() {
        // given
        val notchDeviceManufacturers = arrayOf("pixel" to "3", "google" to "6", "lenevo" to "A52")
        val isProbableNotchDevice = device.firmware.hasNotch(notchDeviceManufacturers)

        // expected
        val expectedWhiteListed = notchDeviceManufacturers.any {
            Build.MANUFACTURER.startsWith(it.first, ignoreCase = true)
                    && Build.MODEL.startsWith(it.second, ignoreCase = true)
        }

        // verify
        assertEquals(expectedWhiteListed, isProbableNotchDevice)
    }

    @Test
    fun given_device_thenVerify_inManufacturers_returnTrue_ifItPresents() {
        // given
        val manufacturers = listOf("pixel", "google", "lenevo")
        val listed = device.firmware.inManufactures(manufacturers)

        // expected
        val expectedListed = manufacturers.any {
            Build.MANUFACTURER.startsWith(it, ignoreCase = true)
        }

        // verify
        assertEquals(expectedListed, listed)
    }

    @Test
    fun given_device_thenVerify_inModels_returnTrue_ifItPresents() {
        // given
        val models = listOf("one-plus-one", "one-plus-two", "one-plus-three")
        val listed = device.firmware.inModels(models)

        // expected
        val expectedListed = models.any {
            Build.MODEL.startsWith(it, ignoreCase = true)
        }

        // verify
        assertEquals(expectedListed, listed)
    }

    @Test
    fun given_osDefaultLanguage_thenVerify_itIs_In_ExpectedFormat() {
        val os = device.os

        assertNotNull(Locale.getDefault().toLanguageTag())
        assertTrue(os.defaultLocale?.asTwoLetterTag() == Locale.getDefault().toLanguageTag())
    }

    @Test
    fun given_apiLevel_thenVerify_resolveOsVersionName_shouldReturnCorrect_versionName() {
        val os = device.os
        val mapApiToVersionName = mapOf(
            1 to "BASE",
            2 to "BASE_1_1",
            3 to "CUPCAKE",
            4 to "DONUT",
            5 to "ECLAIR",
            6 to "ECLAIR_0_1",
            7 to "ECLAIR_MR1",
            8 to "FROYO",
            9 to "GINGERBREAD",
            10 to "GINGERBREAD_MR1",
            11 to "HONEYCOMB",
            12 to "HONEYCOMB_MR1",
            13 to "HONEYCOMB_MR2",
            14 to "ICE_CREAM_SANDWICH",
            15 to "ICE_CREAM_SANDWICH_MR1",
            16 to "JELLY_BEAN",
            17 to "JELLY_BEAN_MR1",
            18 to "JELLY_BEAN_MR2",
            19 to "KITKAT",
            20 to "KITKAT_WATCH",
            21 to "LOLLIPOP",
            22 to "LOLLIPOP_MR1",
            23 to "M",
            24 to "N",
            25 to "N_MR1",
            26 to "O",
            27 to "O_MR1",
            28 to "P",
            29 to "Q",
            30 to "R",
            31 to "S",
            32 to "S_V2",
            33 to "TIRAMISU",
        )

        assertEquals(os.versionName, mapApiToVersionName[os.apiLevel])
    }


    @Test
    fun given_os_thenVerify_themeLabel_returnExpectedLabel() {
        val os = device.os
        val context = InstrumentationRegistry.getInstrumentation()
            .targetContext.applicationContext as Context

        val actualLabel = os.themeLabel(context)
        val isNightMode = (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        val expectedLabel = if (isNightMode) OperatingSystem.DARK_THEME else OperatingSystem.LIGHT_THEME

        assertEquals(expectedLabel, actualLabel)
    }

    @Test
    fun given_os_thenVerify_validate_shouldNotThrow_exception() {
        val os = device.os

        val validated = try {
            os.validate()
            true
        } catch (e: Exception) {
            false
        }

        assertTrue(validated)
    }

    @Test
    fun given_firmwareModel_thenVerify_itIs_BuildModel() {
        val firmware = device.firmware

        assertNotNull(Build.MODEL)
        assertTrue(firmware.model == Build.MODEL)
    }

    @Test
    fun given_firmwareManufacturer_thenVerify_itIs_BuildManufacturer() {
        val firmware = device.firmware

        assertNotNull(Build.MANUFACTURER)
        assertTrue(firmware.manufacturer == Build.MANUFACTURER)
    }

    @Test
    fun given_firmware_thenVerify_validate_shouldNotThrow_exception() {
        val firmware = device.firmware

        val validated = try {
            firmware.validate()
            true
        } catch (e: Exception) {
            false
        }

        assertTrue(validated)
    }

}