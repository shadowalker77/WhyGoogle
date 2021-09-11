package ir.ayantech.whygoogle.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import androidx.viewpager2.widget.ViewPager2
import ir.ayantech.whygoogle.fragment.WhyGoogleFragment
import ir.ayantech.whygoogle.helper.makeItForceRtl
import ir.ayantech.whygoogle.helper.viewBinding
import ir.ayantech.whygoogle.standard.IOSPageTransition
import ir.ayantech.whygoogle.standard.WhyGoogleInterface

abstract class SwipableWhyGoogleActivity<T : ViewBinding> : AppCompatActivity(),
    WhyGoogleInterface {
    val binding: T by viewBinding(binder)

    abstract val binder: (LayoutInflater) -> T

    abstract val fragmentHost: ViewPager2

    private val whyGoogleFragmentAdapter: WhyGoogleFragmentAdapter by lazy {
        WhyGoogleFragmentAdapter(this).also {
            fragmentHost.rotationY = 180f
            fragmentHost.adapter = it
            fragmentHost.makeItForceRtl()
            fragmentHost.setPageTransformer(IOSPageTransition())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fragmentHost.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    if (fragmentHost.currentItem == getFragmentCount() - 2) {
                        fragmentStack.removeLast()
                        whyGoogleFragmentAdapter.notifyItemRemoved(getFragmentCount())
                    }
                    onTopFragmentChanged(fragmentStack.last())
                }
            }
        })
    }

    fun accessViews(block: T.() -> Unit) {
        binding.apply {
            block()
        }
    }

    val fragmentStack = ArrayList<WhyGoogleFragment<*>>()

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
    }

    fun start(fragment: WhyGoogleFragment<*>) {
        start(fragment, false, true)
    }

    override fun start(fragment: WhyGoogleFragment<*>, popAll: Boolean, stack: Boolean) {
        fragmentStack.add(fragment)
        whyGoogleFragmentAdapter.notifyItemInserted(getFragmentCount() - 1)
        fragmentHost.currentItem = getFragmentCount() - 1
    }

    override fun startWithPop(fragment: WhyGoogleFragment<*>) {
        fragmentStack.removeLast()
        fragmentStack.add(fragment)
        whyGoogleFragmentAdapter.notifyItemChanged(getFragmentCount() - 1)
        fragmentHost.currentItem = getFragmentCount() - 1
    }

    override fun <P> startWithPopTo(fragment: WhyGoogleFragment<*>, target: Class<P>) {
        val previousCount = getFragmentCount()
        while (fragmentStack.last().javaClass != target)
            fragmentStack.removeLast()
        whyGoogleFragmentAdapter.notifyItemRangeRemoved(getFragmentCount() - 1, previousCount - 1)
        fragmentStack.add(fragment)
        whyGoogleFragmentAdapter.notifyItemChanged(getFragmentCount() - 1)
        fragmentHost.currentItem = getFragmentCount() - 1
    }

    override fun <P> popTo(target: Class<P>) {
        val previousCount = getFragmentCount()
        while (fragmentStack.last().javaClass != target)
            fragmentStack.removeLast()
        whyGoogleFragmentAdapter.notifyItemRangeRemoved(getFragmentCount() - 1, previousCount - 1)
    }

    override fun pop() {
        fragmentStack.removeLast()
        whyGoogleFragmentAdapter.notifyItemRemoved(getFragmentCount())
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
}