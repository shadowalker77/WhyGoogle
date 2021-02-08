package ir.ayantech.pishkhan24.helper

import android.view.View

fun View.makeGone() {
    this.visibility = View.GONE
}

fun View.makeVisible() {
    this.visibility = View.VISIBLE
}

fun View.changeVisibility(show: Boolean, force: Boolean = false) {
    if (force) {
        if (show) makeVisible() else makeGone()
    } else {
        if (show && isGone()) makeVisible() else if (!show && isVisible()) makeGone()
    }
}

fun View.isVisible() = this.visibility == View.VISIBLE

fun View.isGone() = this.visibility == View.GONE