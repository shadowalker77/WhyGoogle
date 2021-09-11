package ir.ayantech.whygoogle.helper

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

fun ViewPager2.makeItForceRtl() {
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