package ir.ayantech.whygoogle.helper

import android.view.View
import android.view.ViewGroup
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet

fun ViewGroup.delayedTransition(vararg viewsToExclude: View) {
    TransitionManager.beginDelayedTransition(
        this,
        TransitionSet().apply {
            ordering = TransitionSet.ORDERING_SEQUENTIAL
            addTransition(ChangeBounds())
            addTransition(Fade(Fade.IN))
            viewsToExclude.forEach {
                this.excludeChildren(it, true)
            }
        }
    )
}