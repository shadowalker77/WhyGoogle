package ir.ayantech.whygoogle.helper

import android.os.Handler
import android.os.Looper

fun trying(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun delayed(delay: Long = 500L, block: () -> Unit) {
    Looper.getMainLooper()?.let {
        Handler(it).postDelayed({
            trying {
                block()
            }
        }, delay)
    }
}

fun repeatTryingUntil(condition: () -> Boolean, block: () -> Unit) {
    val handler = Handler(Looper.getMainLooper())
    val runnable = object : Runnable {
        override fun run() {
            trying {
                if (condition()) {
                    block()
                    handler.removeCallbacks(this)
                } else {
                    handler.postDelayed(this, 100)
                }
            }
        }
    }
    handler.postDelayed(runnable, 100)
}

fun repeatEvery(milliSeconds: Long, block: () -> Unit) {
    val handler = Handler(Looper.getMainLooper())
    val runnable = object : Runnable {
        override fun run() {
            try {
                block()
                handler.postDelayed(this, milliSeconds)
            } catch (e: java.lang.Exception) {
                handler.removeCallbacks(this)
            }
        }
    }
    handler.postDelayed(runnable, milliSeconds)
}

typealias SimpleCallBack = () -> Unit

typealias BooleanCallBack = (Boolean) -> Unit

typealias StringCallBack = (String) -> Unit

typealias LongCallBack = (Long) -> Unit

typealias IntCallBack = (Int) -> Unit