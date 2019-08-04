package com.example.instagr.common

import android.text.format.DateUtils
import java.util.*

fun formatRelativeTimestamp(startTime: Date, endTime: Date): CharSequence =
    DateUtils.getRelativeTimeSpanString(
        startTime.time,
        endTime.time,
        DateUtils.SECOND_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE
    ).replace(Regex("\\. ago$"), "")
