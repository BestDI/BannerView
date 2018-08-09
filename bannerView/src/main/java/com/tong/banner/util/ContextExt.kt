@file:JvmName("ContextExt")

package com.tong.banner.util

import android.content.Context

internal fun Context.getStatusHeight(): Int {
    var result: Int = -1
    val identifier = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (identifier > 0) {
        result = resources.getDimensionPixelOffset(identifier)
    }
    return result
}