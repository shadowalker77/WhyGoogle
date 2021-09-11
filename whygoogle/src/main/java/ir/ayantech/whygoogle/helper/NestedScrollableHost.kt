package ir.ayantech.whygoogle.helper

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2

class NestedScrollableHost : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private val parentViewPager: ViewPager2?
        get() {
            var v: View? = parent as? View
            while (v != null && v !is ViewPager2) {
                v = v.parent as? View
            }
            return v as? ViewPager2
        }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        handleInterceptTouchEvent(e)
        return super.onInterceptTouchEvent(e)
    }

    private fun handleInterceptTouchEvent(e: MotionEvent) {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                parentViewPager?.isUserInputEnabled = false
                Log.d("uie", "false")
            }
            MotionEvent.ACTION_UP -> {
                parentViewPager?.isUserInputEnabled = true
                Log.d("uie", "true")
            }
            MotionEvent.ACTION_CANCEL -> {
                parentViewPager?.isUserInputEnabled = true
                Log.d("uie", "true")
            }
        }
    }
}