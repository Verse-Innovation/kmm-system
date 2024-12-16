package io.verse.system

import io.tagd.core.ValidateException
import kotlin.test.Test
import kotlin.test.assertFalse

// todo - move to commonTest once ios implementations are complete
class DeviceTest {

    @Test
    fun `verify_deviceStateIsInvalid_becauseOf_unitTestEnvironment`() {
        val device = Device(null)
        val valid =  try {
            device.validate()
            true
        } catch (e : ValidateException) {
            false
        }
        assertFalse(valid)
    }
}