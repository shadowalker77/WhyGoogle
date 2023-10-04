package ir.ayantech.whygoogle.helper

import androidx.recyclerview.widget.RecyclerView
import com.alirezabdn.whyfinal.widget.NonFinalViewPager2

fun NonFinalViewPager2.getRecyclerView() = this.getChildAt(0) as RecyclerView

fun RecyclerView.changeSnapSpeed(speed: Int) {
    try {
        this::class.java.superclass.getDeclaredField("mMinFlingVelocity").let {
            it.isAccessible = true
            val value = (it.get(this) as? Int)
            it.set(this, speed)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}