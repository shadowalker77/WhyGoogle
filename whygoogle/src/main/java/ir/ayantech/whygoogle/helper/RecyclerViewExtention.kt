package ir.ayantech.whygoogle.helper

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import ir.ayantech.whygoogle.R

private fun RecyclerView.basicSetup() {
    this.isMotionEventSplittingEnabled = false
}

fun RecyclerView.verticalSetup(callBack: SimpleCallBack? = null) {
    this.basicSetup()
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
}

fun RecyclerView.rtlSetup(itemCount: Int = 1, callBack: SimpleCallBack? = null) {
    this.basicSetup()
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

fun RecyclerView.staggeredSetup(
    itemCount: Int,
    orientation: Int,
    callBack: SimpleCallBack? = null
) {
    this.basicSetup()
    layoutManager = object : StaggeredGridLayoutManager(itemCount, orientation) {
        override fun onLayoutChildren(
            recycler: RecyclerView.Recycler?,
            state: RecyclerView.State?
        ) {
            super.onLayoutChildren(recycler, state)
            callBack?.invoke()
        }
    }
}

fun RecyclerView.verticalStaggeredSetup(itemCount: Int, callBack: SimpleCallBack? = null) {
    this.basicSetup()
    staggeredSetup(itemCount, RecyclerView.VERTICAL, callBack)
}

fun RecyclerView.horizontalStaggeredSetup(itemCount: Int, callBack: SimpleCallBack? = null) {
    this.basicSetup()
    staggeredSetup(itemCount, RecyclerView.HORIZONTAL, callBack)
}

fun RecyclerView.ltrSetup(itemCount: Int = 1) {
    this.basicSetup()
    layoutManager = GridLayoutManager(context, itemCount, RecyclerView.VERTICAL, false)
}

fun RecyclerView.horizontalSetup(itemCount: Int = 1) {
    this.basicSetup()
    layoutManager = GridLayoutManager(context, itemCount, RecyclerView.HORIZONTAL, false)
}

fun RecyclerView.horizontalRtlSetup(itemCount: Int = 1) {
    this.basicSetup()
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

fun RecyclerView.addDivider(resource: Int? = null) {
    val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
    itemDecorator.setDrawable(
        ContextCompat.getDrawable(
            context,
            resource ?: R.drawable.default_divider
        )!!
    )
    this.addItemDecoration(itemDecorator)
}