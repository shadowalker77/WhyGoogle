package ir.ayantech.whygoogle.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

abstract class CommonAdapter<T, RowLayout : ViewBinding>(
    items: List<T>,
    onItemClickListener: OnItemClickListener<T>? = null
) : BaseAdapter<T, CommonViewHolder<T, RowLayout>>(items, onItemClickListener) {

    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> RowLayout

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommonViewHolder<T, RowLayout> {
        return CommonViewHolder(
            bindingInflater.invoke(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onItemClickListener
        )
    }
}

open class CommonViewHolder<T, RowLayout : ViewBinding>(
    viewBinding: RowLayout,
    onItemClickListener: OnItemClickListener<T>?
) : BaseViewHolder<T>(viewBinding, onItemClickListener) {
    override val mainView: RowLayout
        get() = super.mainView as RowLayout

    fun accessViews(block: RowLayout.() -> Unit) {
        mainView.apply {
            block()
        }
    }
}