package ir.ayantech.whygoogle.adapter

import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import ir.ayantech.pishkhan24.helper.changeVisibility
import ir.ayantech.pishkhan24.helper.delayedTransition

abstract class ExpandableItemAdapter<T, RowLayout : ViewBinding>(
    items: List<T>,
    private val canCollapseAll: Boolean = true,
    onItemClickListener: OnItemClickListener<T>?
) :
    CommonAdapter<T, RowLayout>(items, onItemClickListener) {

    constructor(
        items: List<T>,
        onItemClickListener: OnItemClickListener<T>?
    ) : this(items, true, onItemClickListener)

    var lastExpandedPosition = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommonViewHolder<T, RowLayout> {
        return super.onCreateViewHolder(parent, viewType).also { holder ->
            holder.registerClickListener(
                if (expandViewHandlerId() != null)
                    holder.itemView.findViewById(expandViewHandlerId()!!)
                else holder.itemView
            ) {
                onItemClickListener?.invoke(
                    itemsToView[holder.adapterPosition],
                    holder.itemView.id,
                    holder.adapterPosition
                )
                (it as ViewGroup).delayedTransition()
                lastExpandedPosition =
                    if (lastExpandedPosition == holder.adapterPosition && canCollapseAll) -1 else {
                        notifyItemChanged(lastExpandedPosition)
                        holder.adapterPosition
                    }
                notifyItemChanged(holder.adapterPosition)
            }
        }
    }

    override fun onBindViewHolder(holder: CommonViewHolder<T, RowLayout>, position: Int) {
        super.onBindViewHolder(holder, position)
        changeExpandAndCollapseStatus(holder, position == lastExpandedPosition)
    }

    open fun changeExpandAndCollapseStatus(
        holder: CommonViewHolder<T, RowLayout>,
        status: Boolean
    ) {
        holder.itemView.findViewById<View>(getExpandedLayoutId()).changeVisibility(status)
    }

    abstract fun getExpandedLayoutId(): Int

    open fun expandViewHandlerId(): Int? = null
}