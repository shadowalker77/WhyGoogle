package ir.ayantech.whygoogle.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import ir.ayantech.whygoogle.databinding.WhyGoogleFragmentContainerBinding
import ir.ayantech.whygoogle.helper.trying
import ir.ayantech.whygoogle.standard.WhyGoogleInterface

typealias ViewBindingInflater = (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding

abstract class WhyGoogleFragment<T : ViewBinding> : Fragment() {

    private var _isUILocked = false

    private var _binding: WhyGoogleFragmentContainerBinding? = null

    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> T

    lateinit var mainBinding: ViewBinding

    var headerBinding: ViewBinding? = null

    var footerBinding: ViewBinding? = null

    open val headerInflater: ViewBindingInflater? = null

    open val footerInflater: ViewBindingInflater? = null

    @Suppress("UNCHECKED_CAST")
    protected val binding: T
        get() = mainBinding as T

    open val recreateOnReturn: Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (_binding != null && !recreateOnReturn)
            return requireNotNull(_binding).root.also {
                onFragmentVisible()
            }
        _binding = WhyGoogleFragmentContainerBinding.inflate(layoutInflater, container, false)
        trying {
            if (!this::mainBinding.isInitialized)
                mainBinding = bindingInflater.invoke(inflater, _binding!!.mainRl, true)
            _binding!!.mainRl.addView(
                mainBinding.root,
                RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
            )
        }
//        _binding!!.mainRl.addView(mainBinding.root)
        headerInflater?.let {
            trying {
                headerBinding = it.invoke(inflater, _binding!!.headerRl, true)
            }
        }
        footerInflater?.let {
            trying {
                footerBinding = it.invoke(inflater, _binding!!.footerRl, true)
            }
        }
        _binding?.dummyToLock?.setOnTouchListener { v, event ->
            _isUILocked
        }
        return requireNotNull(_binding).root.also {
            _binding?.root?.let { preShowProcess(it) }
            onCreate()
            onFragmentVisible()
        }
    }

    fun lockUI() {
        _isUILocked = true
    }

    fun unLockUI() {
        _isUILocked = false
    }

    open fun onFragmentVisible() {
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    open fun onCreate() {}

    open fun preShowProcess(rootView: View) {}

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return activity?.let {
            try {
                AnimationUtils.loadAnimation(it, nextAnim).also {
                    it.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {
                            lockUI()
                        }

                        override fun onAnimationRepeat(animation: Animation?) {}

                        override fun onAnimationEnd(animation: Animation?) {
                            unLockUI()
                            onEnterAnimationEnded()
                        }
                    })
                }
            } catch (e: Exception) {
                unLockUI()
                null
            }
        }
    }

    open fun onEnterAnimationEnded() {}

    open fun getFragmentTransactionAnimation(): WhyGoogleFragmentTransactionAnimation? = null

    fun accessViews(block: T.() -> Unit) {
        binding.apply {
            block()
        }
    }

    open fun onBackPressed(): Boolean {
        return false
    }

    fun pop() {
        (activity as? WhyGoogleInterface)?.pop()
    }

    fun <P> popTo(target: Class<P>) {
        (activity as? WhyGoogleInterface)?.popTo(target)
    }

    fun start(fragment: WhyGoogleFragment<*>, popAll: Boolean = false, stack: Boolean = true) {
        (activity as? WhyGoogleInterface)?.start(fragment, popAll, stack)
    }

    fun startWithPop(fragment: WhyGoogleFragment<*>) {
        (activity as? WhyGoogleInterface)?.startWithPop(fragment)
    }

    fun <P> startWithPopTo(fragment: WhyGoogleFragment<*>, target: Class<P>) {
        (activity as? WhyGoogleInterface)?.startWithPopTo(fragment, target)
    }

    fun popAll() {
        (activity as? WhyGoogleInterface)?.popAll()
    }

    fun lazyStart(fragment: WhyGoogleFragment<*>) {
        (activity as? WhyGoogleInterface)?.lazyStart(fragment)
    }
}