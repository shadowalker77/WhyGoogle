package ir.ayantech.whygoogle.adapter

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ir.ayantech.whygoogle.R

class RecyclerItemTouchHelper(
    dragDirs: Int,
    swipeDirs: Int,
    private val adapter: BaseAdapter<*, *>
) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    var getForegroundLayout: ((viewHolder: RecyclerView.ViewHolder?) -> View?) = {
        it?.itemView?.findViewById(R.id.foregroundRl)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder != null) {
            getDefaultUIUtil().onSelected(getForegroundLayout(viewHolder))
        }
    }

    override fun onChildDrawOver(
        c: Canvas, recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        getForegroundLayout(viewHolder)
            ?: return super.onChildDrawOver(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
        getDefaultUIUtil().onDrawOver(
            c,
            recyclerView,
            getForegroundLayout(viewHolder),
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
        adapter.onSwiping(
            viewHolder,
            if (dX > 0) ItemTouchHelper.LEFT else ItemTouchHelper.RIGHT,
            viewHolder.adapterPosition
        )
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val foregroundView =
            getForegroundLayout(viewHolder)
                ?: return super.clearView(
                    recyclerView,
                    viewHolder
                )
        getDefaultUIUtil().clearView(foregroundView)
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        getForegroundLayout(viewHolder)
            ?: return super.onChildDraw(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
        getDefaultUIUtil().onDraw(
            c,
            recyclerView,
            getForegroundLayout(viewHolder) ?: viewHolder.itemView.findViewById(R.id.foregroundRl),
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onSwiped(viewHolder, direction, viewHolder.adapterPosition)
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return adapter.isLongPressDragEnabled()
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return adapter.isItemViewSwipeEnabled()
    }
}