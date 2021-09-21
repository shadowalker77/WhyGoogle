package ir.ayantech.whygoogle.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import androidx.viewpager2.widget.ViewPager2
import ir.ayantech.whygoogle.custom.AsyncLayoutInflater
import ir.ayantech.whygoogle.fragment.WhyGoogleFragment
import ir.ayantech.whygoogle.helper.changeToNeedsOfWhyGoogle
import ir.ayantech.whygoogle.helper.setCurrentItem
import ir.ayantech.whygoogle.helper.trying
import ir.ayantech.whygoogle.helper.viewBinding
import ir.ayantech.whygoogle.standard.IOSPageTransition
import ir.ayantech.whygoogle.standard.WhyGoogleInterface

abstract class SwipableWhyGoogleActivity<T : ViewBinding> : AppCompatActivity(),
    WhyGoogleInterface {
    val binding: T by viewBinding(binder)

    open val TRANSFORM_DURATION = 350L

    abstract val binder: (LayoutInflater) -> T

    abstract val fragmentHost: ViewPager2

    val fragmentStack = ArrayList<WhyGoogleFragment<*>>()

    private val whyGoogleFragmentAdapter: WhyGoogleFragmentAdapter by lazy {
        WhyGoogleFragmentAdapter(this).also {
            fragmentHost.rotationY = 180f
            fragmentHost.changeToNeedsOfWhyGoogle()
            fragmentHost.setPageTransformer(IOSPageTransition())
            fragmentHost.adapter = it
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fragmentHost.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    val previousCount = getFragmentCount()
                    while (fragmentHost.currentItem <= getFragmentCount() - 2) {
                        fragmentStack.removeLast()
                    }
                    if (previousCount >= fragmentHost.currentItem + 2)
                        whyGoogleFragmentAdapter.notifyItemRangeRemoved(
                            fragmentHost.currentItem + 1,
                            previousCount - fragmentHost.currentItem - 1
                        )
                    onTopFragmentChanged(fragmentStack.last())
                    fragmentStack.last().onFragmentVisible()
                }
            }
        })
    }

    fun accessViews(block: T.() -> Unit) {
        binding.apply {
            block()
        }
    }

    private class WhyGoogleFragmentAdapter(private val fragmentActivity: SwipableWhyGoogleActivity<*>) :
        FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int = fragmentActivity.getFragmentCount()

        override fun createFragment(position: Int): Fragment =
            fragmentActivity.fragmentStack[position]

        override fun onBindViewHolder(
            holder: FragmentViewHolder,
            position: Int,
            payloads: MutableList<Any>
        ) {
            super.onBindViewHolder(holder, position, payloads)
            holder.itemView.rotationY = 180f
        }

        override fun getItemId(position: Int): Long {
            return fragmentActivity.fragmentStack[position].creationEpoch
        }
    }

    fun start(fragment: WhyGoogleFragment<*>) {
        start(fragment, false, true)
    }

    private fun WhyGoogleFragment<*>.asyncInflate(callback: (WhyGoogleFragment<*>) -> Unit) {
        AsyncLayoutInflater(this@SwipableWhyGoogleActivity).inflate(
            this.bindingInflater,
            null
        ) { viewBinding, parent ->
            parent?.addView(viewBinding.root)
            (viewBinding).let {
                mainBinding = it
            }
            callback(this)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun start(fragment: WhyGoogleFragment<*>, popAll: Boolean, stack: Boolean) {
        fragment.asyncInflate { fragment ->
            fragmentStack.add(fragment)
            val position = getFragmentCount() - 1
            whyGoogleFragmentAdapter.notifyItemInserted(position)
            if (popAll) {
                fragmentHost.setCurrentItem(position, false)
                fragmentStack.removeAll { it != fragment }
                whyGoogleFragmentAdapter.notifyItemRangeRemoved(0, position)
            } else {
                fragmentHost.setCurrentItem(position, TRANSFORM_DURATION)
            }
        }
    }

    override fun startWithPop(fragment: WhyGoogleFragment<*>) {
        fragmentStack.removeLast()
        fragmentStack.add(fragment)
        whyGoogleFragmentAdapter.notifyItemChanged(getFragmentCount() - 1)
        fragmentHost.setCurrentItem(getFragmentCount() - 1, TRANSFORM_DURATION)
    }

    override fun <P> startWithPopTo(fragment: WhyGoogleFragment<*>, target: Class<P>) {
        val previousCount = getFragmentCount()
        while (fragmentStack.last().javaClass != target)
            fragmentStack.removeLast()
        whyGoogleFragmentAdapter.notifyItemRangeRemoved(getFragmentCount() - 1, previousCount - 1)
        fragmentStack.add(fragment)
        whyGoogleFragmentAdapter.notifyItemChanged(getFragmentCount() - 1)
        fragmentHost.setCurrentItem(getFragmentCount() - 1, TRANSFORM_DURATION)
    }

    override fun <P> popTo(target: Class<P>) {
        trying {
            fragmentStack.reversed().indexOfFirst { it.javaClass == target }.let {
                fragmentHost.setCurrentItem(it, TRANSFORM_DURATION)
            }
        }
    }

    override fun pop() {
        fragmentHost.setCurrentItem(getFragmentCount() - 2, TRANSFORM_DURATION)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun popAll() {
        fragmentStack.clear()
        whyGoogleFragmentAdapter.notifyDataSetChanged()
    }

    override fun onTopFragmentChanged(whyGoogleFragment: WhyGoogleFragment<*>) {
    }

    override fun getTopFragment(): WhyGoogleFragment<*>? =
        if (fragmentStack.isEmpty()) null else fragmentStack.last()

    override fun getFragmentCount(): Int = fragmentStack.size

    override fun onBackPressed() {
        when {
            fragmentStack.lastOrNull()?.onBackPressed() == true -> {
            }
            getFragmentCount() > 1 -> trying { pop() }
            else -> ActivityCompat.finishAfterTransition(this)
        }
    }
}