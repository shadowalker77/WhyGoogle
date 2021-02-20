package ir.ayantech.whygoogle.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import ir.ayantech.whygoogle.databinding.RowCommonViewHolderBinding

abstract class SwipeableItemAdapter<T, RowLayout : ViewBinding, BackLayout : ViewBinding>(
    items: List<T>,
    onItemClickListener: OnItemClickListener<T>? = null
) : BaseAdapter<T, SwipeAbleViewHolder<T, RowLayout, BackLayout>>(
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

    abstract val rowLayoutBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> RowLayout

    abstract val backLayoutBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> BackLayout

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SwipeAbleViewHolder<T, RowLayout, BackLayout> {
        val wholeView =
            RowCommonViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val rowView =
            rowLayoutBindingInflater.invoke(
                LayoutInflater.from(parent.context),
                wholeView.foregroundRl,
                false
            )
        val backView =
            backLayoutBindingInflater.invoke(
                LayoutInflater.from(parent.context),
                wholeView.backgroundRl,
                false
            )
        wholeView.backgroundRl.addView(backView.root)
        wholeView.foregroundRl.addView(rowView.root)
        return SwipeAbleViewHolder(
            wholeView,
            rowView,
            backView,
            onItemClickListener
        )
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