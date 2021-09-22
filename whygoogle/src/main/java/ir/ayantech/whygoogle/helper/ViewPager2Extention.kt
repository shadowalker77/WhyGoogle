package ir.ayantech.whygoogle.helper

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
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

fun NonFinalViewPager2.setCurrentItem(
    item: Int,
    duration: Long,
    interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
    pagePxWidth: Int = width // Default value taken from getWidth() from ViewPager2 view
) {
    val pxToDrag: Int = pagePxWidth * (item - currentItem)
    val animator = ValueAnimator.ofInt(0, pxToDrag)
    var previousValue = 0
    animator.addUpdateListener { valueAnimator ->
        val currentValue = valueAnimator.animatedValue as Int
        val currentPxToDrag = (currentValue - previousValue).toFloat()
        fakeDragBy(-currentPxToDrag)
        previousValue = currentValue
    }
    animator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {
            beginFakeDrag()
        }

        override fun onAnimationEnd(animation: Animator?) {
            endFakeDrag()
        }

        override fun onAnimationCancel(animation: Animator?) { /* Ignored */
        }

        override fun onAnimationRepeat(animation: Animator?) { /* Ignored */
        }
    })
    animator.interpolator = interpolator
    animator.duration = duration
    animator.start()
}