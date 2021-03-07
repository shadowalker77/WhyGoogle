package ir.ayantech.whygoogle.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import ir.ayantech.whygoogle.databinding.RowCommonViewHolderBinding
import ir.ayantech.whygoogle.fragment.ViewBindingInflater

abstract class CommonAdapter<T, RowLayout : ViewBinding>(
    items: List<T>,
    onItemClickListener: OnItemClickListener<T>? = null
) : BaseAdapter<T, CommonViewHolder<T, RowLayout>>(items, onItemClickListener) {

    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> RowLayout

    open val backLayoutBindingInflater: ViewBindingInflater? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommonViewHolder<T, RowLayout> {
        val wholeView =
            RowCommonViewHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val rowView =
            bindingInflater.invoke(
                LayoutInflater.from(parent.context),
                wholeView.foregroundRl,
                false
            )
        val backView =
            backLayoutBindingInflater?.invoke(
                LayoutInflater.from(parent.context),
                wholeView.backgroundRl,
                false
            )
        if (backView != null)
            wholeView.backgroundRl.addView(backView.root)
        wholeView.foregroundRl.addView(rowView.root)
        return CommonViewHolder(wholeView, rowView, backView, onItemClickListener)
    }
}

open class CommonViewHolder<T, RowLayout : ViewBinding>(
    wholeView: RowCommonViewHolderBinding,
    val rowViewBinding: RowLayout,
    val backViewBinding: ViewBinding?,
    onItemClickListener: OnItemClickListener<T>?
) : BaseViewHolder<T>(wholeView, onItemClickListener) {

    constructor(
        wholeView: RowCommonViewHolderBinding,
        rowViewBinding: RowLayout,
        onItemClickListener: OnItemClickListener<T>?
    ) : this(wholeView, rowViewBinding, null, onItemClickListener)

    override val mainView: RowLayout
        get() = super.mainView as RowLayout

    fun accessViews(block: RowLayout.() -> Unit) {
        mainView.apply {
            block()
        }
    }
}