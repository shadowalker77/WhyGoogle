package ir.ayantech.whygoogle.helper

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.alirezabdn.whyfinal.widget.NonFinalViewPager2

fun NonFinalViewPager2.changeToNeedsOfWhyGoogle() {
    this.getChildAt(0).let { rv ->
        (rv as? RecyclerView)?.changeSnapSpeed()
        rv.overScrollMode = View.OVER_SCROLL_NEVER
    }
}

fun RecyclerView.changeSnapSpeed() {
    try {
        this::class.java.superclass.getDeclaredField("mMinFlingVelocity").let {
            it.isAccessible = true
            val value = (it.get(this) as? Int)
            it.set(this, value!! * 10)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}