package ir.ayantech.whygoogle.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import ir.ayantech.whygoogle.R
import ir.ayantech.whygoogle.helper.changeVisibility
import ir.ayantech.whygoogle.helper.delayedTransition

abstract class ExpandableItemAdapter<T, RowLayout : ViewBinding>(
    items: List<T>,
    private var canCollapseAll: Boolean = true,
    onItemClickListener: OnItemClickListener<T>?
) :
    CommonAdapter<T, RowLayout>(items, onItemClickListener) {

    constructor(
        items: List<T>,
        onItemClickListener: OnItemClickListener<T>?
    ) : this(items, true, onItemClickListener)

    var lastExpandedPosition = 0

    open val hasRightIndicator = true

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommonViewHolder<T, RowLayout> {
        return super.onCreateViewHolder(parent, viewType).also { holder ->
            if (hasRightIndicator)
                holder.wholeView.backgroundRl.setBackgroundColor(
                    ContextCompat.getColor(
                        parent.context,
                        R.color.back_expand
                    )
                )
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
        if (hasRightIndicator)
            holder.rowViewBinding.root.translationX =
                if (status) holder.itemView.context.resources.getDimensionPixelSize(R.dimen.expand_margin)
                    .toFloat() * -1
                else 0f
        holder.itemView.findViewById<View>(getExpandedLayoutId()).changeVisibility(status)
    }

    abstract fun getExpandedLayoutId(): Int

    open fun expandViewHandlerId(): Int? = null
}