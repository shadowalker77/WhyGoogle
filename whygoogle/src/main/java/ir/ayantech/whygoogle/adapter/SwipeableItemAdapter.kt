package ir.ayantech.whygoogle.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import ir.ayantech.whygoogle.databinding.RowCommonViewHolderBinding

abstract class SwipeableItemAdapter<T, RowLayout : ViewBinding, BackLayout : ViewBinding>(
    items: List<T>,
    onItemClickListener: OnItemClickListener<T>? = null
) : ExpandableItemAdapter<T, RowLayout>(
    items,
    onItemClickListener
),
    RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
            RecyclerItemTouchHelper(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                this
            )
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(parentRv)
    }

    override fun onSwiped(
        viewHolder: RecyclerView.ViewHolder?,
        direction: Int,
        position: Int
    ) {
        swiped(itemsToView[position], position)
    }

    open fun swiped(item: T, position: Int) {}
}

class SwipeAbleViewHolder<T, RowLayout : ViewBinding, BackLayout : ViewBinding>(
    wholeView: RowCommonViewHolderBinding,
    val rowViewBinding: RowLayout,
    val backViewBinding: BackLayout,
    onItemClickListener: OnItemClickListener<T>?
) : BaseViewHolder<T>(wholeView, onItemClickListener)