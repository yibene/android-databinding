/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:JvmName("Converter")
package com.example.android.databinding.twowaysample.util

import android.util.Log

fun fromTenthsToSeconds(tenths: Float) : String {
    Log.w("Cash", "fromTenthsToSeconds: tenths = $tenths")
    return if (tenths < 600) {
        String.format("%.1f", tenths / 10.0f)
    } else {
        val minutes = ((tenths / 10) / 60).toInt()
        val seconds = ((tenths / 10) % 60).toInt()
        String.format("%d:%02d", minutes, seconds)
    }
}

fun cleanSecondsString(seconds: String): Float {
    Log.w("Cash", "cleanSecondsString: $seconds")
    // Remove letters and other characters
    val filteredValue = seconds.replace(Regex("""[^\d:.]"""), "")
    Log.w("Cash", "filteredValue = $filteredValue")
    if (filteredValue.isEmpty()) return 0f
    val elements: List<Float> = filteredValue.split(":").map { it.toFloat() }
    Log.w("Cash", "elements = $elements")
    var result: Float
    return when {
        elements.size > 2 -> 0f
        elements.size > 1 -> {
            result = elements[0] * 60
            result += elements[1]
            result * 10
        }
        else -> elements[0] * 10
    }
}
