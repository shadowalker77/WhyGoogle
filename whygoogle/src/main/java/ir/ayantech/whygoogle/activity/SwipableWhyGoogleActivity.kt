package ir.ayantech.whygoogle.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.alirezabdn.whyfinal.adapter.FragmentStateAdapter
import com.alirezabdn.whyfinal.adapter.FragmentViewHolder
import ir.ayantech.whygoogle.custom.AsyncLayoutInflater
import ir.ayantech.whygoogle.fragment.WhyGoogleFragment
import ir.ayantech.whygoogle.helper.changeToNeedsOfWhyGoogle
import ir.ayantech.whygoogle.helper.trying
import ir.ayantech.whygoogle.helper.viewBinding
import ir.ayantech.whygoogle.standard.IOSPageTransition
import ir.ayantech.whygoogle.standard.WhyGoogleInterface
import ir.ayantech.whygoogle.widget.SwipeBackContainer

abstract class SwipableWhyGoogleActivity<T : ViewBinding> : AppCompatActivity(),
    WhyGoogleInterface {
    val binding: T by viewBinding(binder)

    open val TRANSFORM_DURATION = 350L

    abstract val binder: (LayoutInflater) -> T

    abstract val fragmentHost: SwipeBackContainer

    val fragmentStack = ArrayList<WhyGoogleFragment<*>>()

    private val whyGoogleFragmentAdapter: WhyGoogleFragmentAdapter by lazy {
        WhyGoogleFragmentAdapter(this).also {
            fragmentHost.offscreenPageLimit = 3
            fragmentHost.rotationY = 180f
            fragmentHost.changeToNeedsOfWhyGoogle()
            fragmentHost.setPageTransformer(IOSPageTransition())
            fragmentHost.adapter = it
        }
    }

    private var lastKnownFragment: WhyGoogleFragment<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fragmentHost.onPageSettled {
            val previousCount = getFragmentCount()
            while (fragmentHost.currentItem <= getFragmentCount() - 2) {
                fragmentStack.removeLast()
            }
            if (previousCount >= fragmentHost.currentItem + 2) {
                whyGoogleFragmentAdapter.notifyItemRangeRemoved(
                    fragmentHost.currentItem + 1,
                    previousCount - fragmentHost.currentItem - 1
                )
                fragmentStack.lastOrNull()?.onFragmentVisible()
                fragmentStack.lastOrNull()?.onBackToFragment()
                onTopFragmentChanged(fragmentStack.last())
            }
            if (lastKnownFragment == fragmentStack.lastOrNull())
                return@onPageSettled
            fragmentStack.lastOrNull()?.onEnterAnimationEnded()
            lastKnownFragment = fragmentStack.lastOrNull()
        }
    }

    fun accessViews(block: T.() -> Unit) {
        binding.apply {
            block()
        }
    }

    internal class WhyGoogleFragmentAdapter(val fragmentActivity: SwipableWhyGoogleActivity<*>) :
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
        if (getFragmentCount() == 1 && fragment.javaClass == getTopFragment()?.javaClass)
            return
        fragment.asyncInflate { fragment ->
            fragmentStack.add(fragment)
            val position = getFragmentCount() - 1
            whyGoogleFragmentAdapter.notifyItemInserted(position)
            (fragment.mainBinding.root as? ViewGroup)?.let {
                it.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        if (popAll) {
                            fragmentHost.setCurrentItem(position, false)
                            fragmentStack.removeAll { it != fragment }
                            whyGoogleFragmentAdapter.notifyItemRangeRemoved(0, position)
                        } else {
                            fragmentHost.setCurrentItem(position, TRANSFORM_DURATION)
                        }
                        it.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
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
            getFragmentCount() > 1 -> trying {
                pop()
            }
            else -> ActivityCompat.finishAfterTransition(this)
        }
    }
}