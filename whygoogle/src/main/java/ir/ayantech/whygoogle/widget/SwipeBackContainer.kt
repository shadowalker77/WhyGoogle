package ir.ayantech.whygoogle.widget

import android.animation.TimeInterpolator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import com.alirezabdn.whyfinal.widget.NonFinalViewPager2
import ir.ayantech.whygoogle.helper.SimpleCallBack

abstract class SwipeBackContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : NonFinalViewPager2(context, attrs) {
    abstract fun onPageSettled(callback: SimpleCallBack)

    abstract fun setCurrentItem(
        item: Int,
        duration: Long,
        interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
        pagePxWidth: Int = width, // Default value taken from getWidth() from ViewPager2 view
        onFragmentCreationEndedCallback: SimpleCallBack? = null
    )
}