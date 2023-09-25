package ir.ayantech.whygoogle.standard

import android.view.View
import com.alirezabdn.whyfinal.widget.NonFinalViewPager2

class IOSPageTransition(private val isRtl: Boolean) : NonFinalViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        when {
            position < -1 -> page.alpha = 0f
            position <= 0 -> {
                page.alpha = 1f
                page.translationX = ((if(isRtl) 1 else -1) * position) / 1.3f * page.width
                page.alpha = (position * 0.2f + 1f)
            }
            position <= 1 -> page.alpha = 1f
            else -> page.alpha = 0f

        }
    }
}