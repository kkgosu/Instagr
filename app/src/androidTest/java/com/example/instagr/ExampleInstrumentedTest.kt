package com.example.instagr

import androidx.test.runner.AndroidJUnit4
import com.example.instagr.common.formatRelativeTimestamp

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class TestUtilsTest {
    private val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, 2019)
        set(Calendar.MONTH, 0)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    @Test
    fun shouldFormatRelativeTime() {
        val startTime = calendar.time
        val endTime = calendar.change { add(Calendar.SECOND, 10) }.time
        val endTimeMin = calendar.change { add(Calendar.MINUTE, 10) }.time
        assertEquals("10 сек. назад", formatRelativeTimestamp(startTime, endTime))
        assertEquals("10 мин. назад", formatRelativeTimestamp(startTime, endTimeMin))
    }

    private fun Calendar.change(f: Calendar.() -> Unit): Calendar {
        val newCalendar = clone() as Calendar
        f(newCalendar)
        return newCalendar
    }
}
