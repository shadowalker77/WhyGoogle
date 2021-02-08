package ir.ayantech.whygoogle.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import ir.ayantech.whygoogle.fragment.ViewBindingInflater

abstract class MultiViewTypeAdapter<T>(
    items: List<T>,
    onItemClickListener: OnItemClickListener<T>? = null
) :
    BaseAdapter<T, MultiViewTypeViewHolder<T>>(items, onItemClickListener) {

    abstract fun getViewInflaterForViewType(viewType: Int): ViewBindingInflater

//    abstract fun getViewBindingForViewType(viewType: Int): Class<out ViewBinding>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiViewTypeViewHolder<T> {
        return MultiViewTypeViewHolder(
            getViewInflaterForViewType(viewType).invoke(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClickListener
        )
    }
}

class MultiViewTypeViewHolder<T>(
    viewBinding: ViewBinding,
    onItemClickListener: OnItemClickListener<T>?
) : BaseViewHolder<T>(viewBinding, onItemClickListener)