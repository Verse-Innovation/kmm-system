@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.verse.system

actual class SystemClock actual constructor() : Clock {
    override fun currentTimeMillis(): Long {
        TODO("Not yet implemented")
    }

    override fun elapsedRealtime(): Long {
        TODO("Not yet implemented")
    }

    override fun uptimeMillis(): Long {
        TODO("Not yet implemented")
    }

    override fun sleep(sleepTimeMs: Long) {
        TODO("Not yet implemented")
    }
}