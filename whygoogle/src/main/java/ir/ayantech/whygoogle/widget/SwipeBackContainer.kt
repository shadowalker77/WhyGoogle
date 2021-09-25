package ir.ayantech.whygoogle.widget

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.alirezabdn.whyfinal.widget.NonFinalViewPager2
import ir.ayantech.whygoogle.activity.SwipableWhyGoogleActivity
import kotlin.math.abs
import kotlin.math.sign

class SwipeBackContainer : NonFinalViewPager2 {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    private var touchSlop = 0
    private var initialX = 0f
    private var initialY = 0f

    private fun getFirstScrollableChild(
        viewToCheck: ViewGroup? = (this.adapter as? SwipableWhyGoogleActivity.WhyGoogleFragmentAdapter)?.fragmentActivity?.getTopFragment()?.mainBinding?.root as? ViewGroup,
        x: Float, y: Float
    ): View? {
        if (viewToCheck == null) return null
        val orientation = this.orientation
        for (i in (viewToCheck.childCount - 1) downTo 0) {
            val childToCheck = viewToCheck.getChildAt(i)
            val bounds = Rect()
            childToCheck.getHitRect(bounds)
            if (!bounds.contains(x.toInt(), y.toInt())) continue
            if (canViewScroll(childToCheck, orientation, -1f) || canViewScroll(
                    childToCheck,
                    orientation,
                    1f
                )
            )
                return childToCheck
            if (childToCheck is ViewGroup) {
                val grandChild = getFirstScrollableChild(childToCheck, x, y)
                if (grandChild != null) return grandChild
            }
        }
        return null
    }

    init {
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    private fun canViewScroll(view: View?, orientation: Int, delta: Float): Boolean {
        val direction = -delta.sign.toInt()
        return when (orientation) {
            0 -> view?.canScrollHorizontally(direction) ?: false
            1 -> view?.canScrollVertically(direction) ?: false
            else -> throw IllegalArgumentException()
        }
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        handleInterceptTouchEvent(e)
        return super.onInterceptTouchEvent(e)
    }

    private fun handleInterceptTouchEvent(e: MotionEvent) {
        val orientation = this.orientation

        val firstScrollableChild = getFirstScrollableChild(x = e.x, y = e.y)
        // Early return if child can't scroll in same direction as parent
        if (!canViewScroll(firstScrollableChild, orientation, -1f) && !canViewScroll(
                firstScrollableChild,
                orientation,
                1f
            )
        ) {
            isUserInputEnabled = true
            return
        }

        if (firstScrollableChild == null) {
            isUserInputEnabled = true
            return
        }

        if (e.action == MotionEvent.ACTION_DOWN) {
            initialX = e.x
            initialY = e.y
        } else if (e.action == MotionEvent.ACTION_MOVE) {
            val dx = e.x - initialX
            val dy = e.y - initialY

            if (abs(dy) > abs(dx)) {
                isUserInputEnabled = true
                firstScrollableChild.parent?.requestDisallowInterceptTouchEvent(false)
            } else {
                if (canViewScroll(firstScrollableChild, orientation, -dx)) {
                    isUserInputEnabled = false
                    firstScrollableChild.parent?.requestDisallowInterceptTouchEvent(true)
                } else {
                    isUserInputEnabled = true
                    firstScrollableChild.parent?.requestDisallowInterceptTouchEvent(false)
                }
            }
        }
    }
}