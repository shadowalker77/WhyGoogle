package ir.ayantech.whygoogle.standard

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class IOSPageTransition : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        when {
            position < -1 -> {     // [-Infinity,-1)
                page.alpha = 0f
            }
            position <= 0 -> {    // [-1,0]
                page.alpha = 1f
                page.translationX = position / 1.3f * page.width
                page.scaleX = (position * 0.03f + 1f)
                page.scaleY = (position * 0.03f + 1f)
                page.alpha = (position * 0.5f + 1f)
//                page.rotationY = 180 * (1 - abs(position) + 1)
            }
            position <= 1 -> {    // (0,1]
                page.alpha = 1f
//                page.translationX = -position * page.width
//                page.rotationY = -180 * (1 - abs(position) + 1)
            }
            else -> {
                page.alpha = 0f
            }
        }
    }
}