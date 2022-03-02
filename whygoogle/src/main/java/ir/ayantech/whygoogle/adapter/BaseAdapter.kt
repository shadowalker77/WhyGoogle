package ir.ayantech.whygoogle.adapter

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kotlin.random.Random

typealias OnItemClickListener<T> = (item: T?, viewId: Int, position: Int) -> Unit

abstract class BaseAdapter<T, ViewHolder : BaseViewHolder<T>>(
    var items: List<T>,
    protected val onItemClickListener: OnItemClickListener<T>? = null
) :
    RecyclerView.Adapter<ViewHolder>() {

    var parentRv: RecyclerView? = null
    var itemTouchHelperCallback: RecyclerItemTouchHelper? = null
    var itemTouchHelper: ItemTouchHelper? = null

    open val dragDirections = 0
    open val swipeDirections = 0

    override fun getItemId(position: Int): Long {
        return itemsToView[position].hashCode().toLong()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.parentRv = recyclerView
        this.itemTouchHelperCallback =
            RecyclerItemTouchHelper(dragDirections, swipeDirections, this@BaseAdapter)
        this.itemTouchHelper =
            ItemTouchHelper(itemTouchHelperCallback!!).also { it.attachToRecyclerView(parentRv) }
    }

    fun filterItems(condition: (T) -> Boolean) {
        itemsToView = items.filter { condition(it) }
        notifyDataSetChanged()
    }

    open fun isLongPressDragEnabled(): Boolean {
        return dragDirections != 0
    }

    open fun isItemViewSwipeEnabled(): Boolean {
        return swipeDirections != 0
    }

    open fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        return true
    }

    open fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item = itemsToView[position]
    }

    var itemsToView = items

    override fun getItemCount() = itemsToView.size
}

open class BaseViewHolder<T>(
    val viewBinding: ViewBinding,
    private val onItemClickListener: OnItemClickListener<T>?
) : RecyclerView.ViewHolder(viewBinding.root), View.OnClickListener {

    var item: T? = null

    var rotate: ObjectAnimator? = null

    fun initRotate() {
        rotate = ObjectAnimator.ofFloat(this.itemView, "rotation", -1.5f, 1.5f)
        (rotate as ObjectAnimator).duration = 180
        (rotate as ObjectAnimator).repeatMode = ValueAnimator.REVERSE
        (rotate as ObjectAnimator).repeatCount = ValueAnimator.INFINITE
        (rotate as ObjectAnimator).startDelay =
            Random(System.currentTimeMillis()).nextLong(0L, 350L)
    }

    val flags = arrayListOf<Boolean>()

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        onItemClickListener?.invoke(item, p0?.id ?: -1, adapterPosition)
    }

    fun registerClickListener(view: View, onClickListener: View.OnClickListener? = null) {
        view.setOnClickListener(onClickListener ?: this)
    }
}