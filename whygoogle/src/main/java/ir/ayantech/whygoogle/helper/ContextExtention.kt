package ir.ayantech.whygoogle.helper

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

fun Context.getDrawableCompat(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)

fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)