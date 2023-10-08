package ir.ayantech.whygoogle.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.alirezabdn.whyfinal.widget.NonFinalViewPager2
import ir.ayantech.whygoogle.custom.AsyncLayoutInflater
import ir.ayantech.whygoogle.custom.MyFragmentStateAdapter
import ir.ayantech.whygoogle.custom.MyFragmentViewHolder
import ir.ayantech.whygoogle.fragment.WhyGoogleFragment
import ir.ayantech.whygoogle.helper.*
import ir.ayantech.whygoogle.standard.IOSPageTransition
import ir.ayantech.whygoogle.standard.LaunchMode
import ir.ayantech.whygoogle.standard.WhyGoogleInterface
import ir.ayantech.whygoogle.widget.SwipeBackContainer

abstract class SwipableWhyGoogleActivity<T : ViewBinding> : AppCompatActivity(),
    WhyGoogleInterface {
    val binding: T by viewBinding(binder)

    open val TRANSFORM_DURATION = 350L

    abstract val binder: (LayoutInflater) -> T

    abstract val fragmentHost: SwipeBackContainer

    val fragmentStack = ArrayList<WhyGoogleFragment<*>>()

    open val forceRtl = false

    open val directionCareRtl = false

    private var defaultFling: Int = 100

    private val whyGoogleFragmentAdapter: WhyGoogleFragmentAdapter by lazy {
        WhyGoogleFragmentAdapter(this, forceRtl).also {
            fragmentHost.offscreenPageLimit = 3
            if (forceRtl)
                fragmentHost.rotation = 180f
            fragmentHost.getRecyclerView()::class.java.superclass.getDeclaredField("mMinFlingVelocity")
                .let {
                    it.isAccessible = true
                    defaultFling = ((it.get(fragmentHost.getRecyclerView()) as? Int) ?: 88) * 30
                }
            fragmentHost.getRecyclerView().overScrollMode = View.OVER_SCROLL_NEVER
            fragmentHost.getRecyclerView().changeSnapSpeed(defaultFling)
            fragmentHost.setPageTransformer(pageTransformer)
            fragmentHost.adapter = it
        }
    }

    open val pageTransformer: NonFinalViewPager2.PageTransformer =
        IOSPageTransition(directionCareRtl)

    private var lastKnownFragment: WhyGoogleFragment<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (directionCareRtl) {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
        fragmentHost.listener(
            onPageSettled = {
                val previousCount = getFragmentCount()
                if (fragmentHost.currentItem <= getFragmentCount() - 2 && getTopFragment()?.preventFromPop == true) {
                    fragmentHost.setCurrentItem(getFragmentCount() - 1, true)
                    getTopFragment()?.onBackPressed()
                    return@listener
                }
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
                if (lastKnownFragment != fragmentStack.lastOrNull()) {
                    fragmentStack.lastOrNull()?.onEnterAnimationEnded()
                    lastKnownFragment = fragmentStack.lastOrNull()
                }
                transactioning = false
                executeLastTransaction()
            },
            onPageScrolled = {}
        )
    }

    fun accessViews(block: T.() -> Unit) {
        binding.apply {
            block()
        }
    }

    internal class WhyGoogleFragmentAdapter(
        val fragmentActivity: SwipableWhyGoogleActivity<*>,
        val forceRtl: Boolean
    ) :
        MyFragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = fragmentActivity.getFragmentCount()

        override fun createFragment(position: Int): Fragment =
            fragmentActivity.fragmentStack[position]

        override fun onBindViewHolder(
            holder: MyFragmentViewHolder,
            position: Int,
            payloads: MutableList<Any>
        ) {
            super.onBindViewHolder(holder, position, payloads)
            holder.setIsRecyclable(false)
            if (forceRtl)
                holder.itemView.rotation = 180f
        }

        override fun getItemId(position: Int): Long {
            return fragmentActivity.fragmentStack[position].creationEpoch
        }
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

    @Volatile
    private var transactioning = false
    private var transactions = ArrayList<SimpleCallBack>()

    private fun executeLastTransaction() {
        if (transactions.isEmpty())
            return
        synchronized(this) {
            if (transactioning)
                return@synchronized
            transactioning = true
            val transaction = transactions.lastOrNull()
            transaction?.let {
                transactions.remove(it)
                it.invoke()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun start(
        fragment: WhyGoogleFragment<*>,
        popAll: Boolean,
        stack: Boolean,
        launchMode: LaunchMode,
        smoothScrollOnPopAll: Boolean,
        onFragmentCreationEndedCallback: SimpleCallBack?
    ) {
        if ((getFragmentCount() == 1 && fragment.javaClass == getTopFragment()?.javaClass && popAll))
            return
        transactions.add {
            fragment.asyncInflate { fragment ->
                fragmentStack.add(fragment)
                val position = getFragmentCount() - 1
                whyGoogleFragmentAdapter.notifyItemInserted(position)
                (fragment.mainBinding.root as? ViewGroup)?.let {
                    val viewReady: () -> Unit = {
                        if (popAll) {
                            if (smoothScrollOnPopAll) {
                                fragmentHost.setCurrentItem(
                                    item = position,
                                    duration = TRANSFORM_DURATION,
                                    onFragmentCreationEndedCallback = onFragmentCreationEndedCallback
                                )
                            } else {
                                fragmentHost.setCurrentItem(position, false)
                            }
                            fragmentStack.removeAll { it != fragment }
                            whyGoogleFragmentAdapter.notifyItemRangeRemoved(0, position)
                            transactioning = false
                        } else {
                            fragmentHost.setCurrentItem(
                                position,
                                TRANSFORM_DURATION,
                                onFragmentCreationEndedCallback = onFragmentCreationEndedCallback
                            )
                        }
                    }
                    if (fragment.isMainBindingInitialized()) {
                        viewReady()
                        return@let
                    }
                    it.viewTreeObserver.addOnGlobalLayoutListener(object :
                        ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            viewReady()
                            it.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                    })
                }
            }
        }
        executeLastTransaction()
    }

    override fun startWithPop(fragment: WhyGoogleFragment<*>) {
        transactions.add {
            fragmentStack.removeLast()
            fragmentStack.add(fragment)
            whyGoogleFragmentAdapter.notifyItemChanged(getFragmentCount() - 1)
            transactioning = false
        }
        executeLastTransaction()
    }

    override fun <P> startWithPopTo(fragment: WhyGoogleFragment<*>, target: Class<P>) {
        transactions.add {
            val previousCount = getFragmentCount()
            while (fragmentStack.last().javaClass != target)
                fragmentStack.removeLast()
            whyGoogleFragmentAdapter.notifyItemRangeRemoved(
                getFragmentCount() - 1,
                previousCount - 1
            )
            fragmentStack.add(fragment)
            whyGoogleFragmentAdapter.notifyItemChanged(getFragmentCount() - 1)
            fragmentHost.setCurrentItem(getFragmentCount() - 1, TRANSFORM_DURATION)
        }
        executeLastTransaction()
    }

    override fun <P> popTo(target: Class<P>) {
        transactions.add {
            trying {
                fragmentStack.reversed().indexOfFirst { it.javaClass == target }.let {
                    fragmentHost.setCurrentItem(it, TRANSFORM_DURATION)
                }
            }
        }
        executeLastTransaction()
    }

    override fun pop() {
        transactions.add {
            fragmentHost.setCurrentItem(getFragmentCount() - 2, TRANSFORM_DURATION)
        }
        executeLastTransaction()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun popAll() {
        transactions.add {
            fragmentStack.clear()
            whyGoogleFragmentAdapter.notifyDataSetChanged()
            transactioning = false
        }
        executeLastTransaction()
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

    override fun <T : WhyGoogleFragment<*>> getFragmentByClass(target: Class<T>): T? {
        TODO("Not yet implemented")
    }
}