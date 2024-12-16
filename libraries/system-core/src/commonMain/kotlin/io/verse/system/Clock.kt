package io.verse.system

interface Clock {

    /**
     * Returns the current time in milliseconds since the Unix Epoch.
     *
     * @see System.currentTimeMillis
     */
    fun currentTimeMillis(): Long

    /** @see android.os.SystemClock.elapsedRealtime
     */
    fun elapsedRealtime(): Long

    /** @see android.os.SystemClock.uptimeMillis
     */
    fun uptimeMillis(): Long

    /** @see android.os.SystemClock.sleep
     */
    fun sleep(sleepTimeMs: Long)
}