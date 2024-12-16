@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.verse.system

import android.os.SystemClock

actual class SystemClock actual constructor() : Clock {

    override fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    override fun elapsedRealtime(): Long {
        return SystemClock.elapsedRealtime()
    }

    override fun uptimeMillis(): Long {
        return SystemClock.uptimeMillis()
    }

    override fun sleep(sleepTimeMs: Long) {
        SystemClock.sleep(sleepTimeMs)
    }
}