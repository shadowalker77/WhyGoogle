package ir.ayantech.whygoogle.helper

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


fun RecyclerView.verticalSetup(callBack: SimpleCallBack? = null) {
    layoutManager = object : LinearLayoutManager(context, RecyclerView.VERTICAL, false) {
        override fun onLayoutChildren(
            recycler: RecyclerView.Recycler?,
            state: RecyclerView.State?
        ) {
            super.onLayoutChildren(recycler, state)
            if (state?.isMeasuring == false) {
                callBack?.invoke()
            }
        }
    }
//    layoutManager = GridLayoutManager(context, itemCount, RecyclerView.VERTICAL, false)
}

fun RecyclerView.rtlSetup(itemCount: Int = 1, callBack: SimpleCallBack? = null) {
    layoutManager =
        object : RtlGridLayoutManager(context, itemCount, RecyclerView.VERTICAL, false) {
            override fun onLayoutChildren(
                recycler: RecyclerView.Recycler?,
                state: RecyclerView.State?
            ) {
                super.onLayoutChildren(recycler, state)
                if (state?.isMeasuring == false) {
                    Log.d("state", state?.toString() ?: "")
                    callBack?.invoke()
                }
            }
        }
}

fun RecyclerView.ltrSetup(itemCount: Int = 1) {
    layoutManager = GridLayoutManager(context, itemCount, RecyclerView.VERTICAL, false)
}

fun RecyclerView.horizontalRtlSetup(itemCount: Int = 1) {
    layoutManager = RtlGridLayoutManager(context, itemCount, RecyclerView.HORIZONTAL, false)
}

open class RtlGridLayoutManager : GridLayoutManager {
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    constructor(context: Context, spanCount: Int) : super(context, spanCount)

    constructor(context: Context, spanCount: Int, orientation: Int, reverseLayout: Boolean) : super(
        context,
        spanCount,
        orientation,
        reverseLayout
    )

    override fun isLayoutRTL() = true
}

fun RecyclerView.scrollListener(callBack: BooleanCallBack) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 5)
                callBack(false)
            if (dy < -5)
                callBack(true)
        }
    })
}