package ir.ayantech.whygoogle.widget

import android.animation.TimeInterpolator
import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import com.alirezabdn.whyfinal.widget.NonFinalViewPager2
import ir.ayantech.whygoogle.activity.SwipableWhyGoogleActivity
import ir.ayantech.whygoogle.helper.FloatCallBack
import ir.ayantech.whygoogle.helper.SimpleCallBack

abstract class SwipeBackContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : NonFinalViewPager2(context, attrs) {
    abstract fun onPageSettled(callback: SimpleCallBack)

    override fun isUserInputEnabled(): Boolean {
        if ((this.adapter as? SwipableWhyGoogleActivity.WhyGoogleFragmentAdapter)?.fragmentActivity?.getTopFragment()?.lockedSwipe == true)
            return false
        return super.isUserInputEnabled()
    }
    fun listener(
        onPageSettled: SimpleCallBack,
        onPageScrolled: FloatCallBack
    ) {
        this.registerOnPageChangeCallback(object :
            OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                isUserInputEnabled = this@SwipeBackContainer.adapter?.itemCount != 1
                if (state == SCROLL_STATE_IDLE) {
                    onPageSettled()
                }
            }
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                onPageScrolled(positionOffset)
            }
        })
    }

    abstract fun setCurrentItem(
        item: Int,
        duration: Long,
        interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
        pagePxWidth: Int = width, // Default value taken from getWidth() from ViewPager2 view
        onFragmentCreationEndedCallback: SimpleCallBack? = null
    )
}