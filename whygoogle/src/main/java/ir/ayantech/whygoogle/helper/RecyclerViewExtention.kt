package ir.ayantech.whygoogle.helper

import android.content.Context
import android.graphics.drawable.InsetDrawable
import android.util.AttributeSet
import android.util.Log
import androidx.recyclerview.widget.*
import ir.ayantech.whygoogle.R


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

fun RecyclerView.staggeredSetup(
    itemCount: Int,
    orientation: Int,
    callBack: SimpleCallBack? = null
) {
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
    staggeredSetup(itemCount, RecyclerView.VERTICAL, callBack)
}

fun RecyclerView.horizontalStaggeredSetup(itemCount: Int, callBack: SimpleCallBack? = null) {
    staggeredSetup(itemCount, RecyclerView.HORIZONTAL, callBack)
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

fun RecyclerView.addDivider(marginSize: Int? = null) {
    val ATTRS = intArrayOf(android.R.attr.listDivider)
    val a = context.obtainStyledAttributes(ATTRS)
    val divider = a.getDrawable(0)
    val inset = resources.getDimensionPixelSize(marginSize ?: R.dimen.default_divider_margin)
    val insetDivider = InsetDrawable(divider, inset, 0, inset, 0)
    a.recycle()
    val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
    itemDecoration.setDrawable(insetDivider)
    this.addItemDecoration(itemDecoration)
}