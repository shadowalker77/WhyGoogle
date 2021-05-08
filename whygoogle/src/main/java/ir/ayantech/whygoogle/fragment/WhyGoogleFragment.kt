package ir.ayantech.whygoogle.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import ir.ayantech.whygoogle.activity.WhyGoogleActivity
import ir.ayantech.whygoogle.databinding.WhyGoogleFragmentContainerBinding
import ir.ayantech.whygoogle.helper.trying

typealias ViewBindingInflater = (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding

abstract class WhyGoogleFragment<T : ViewBinding> : Fragment() {

    private var _binding: WhyGoogleFragmentContainerBinding? = null

    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> T

    lateinit var mainBinding: T

    var headerBinding: ViewBinding? = null

    var footerBinding: ViewBinding? = null

    open val headerInflater: ViewBindingInflater? = null

    open val footerInflater: ViewBindingInflater? = null

    @Suppress("UNCHECKED_CAST")
    protected val binding: T
        get() = mainBinding

    open val recreateOnReturn: Boolean = false

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
            mainBinding = bindingInflater.invoke(inflater, _binding!!.mainRl, true)
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
        _binding?.root?.let { preShowProcess(it) }
        return requireNotNull(_binding).root.also {
            onCreate()
            onFragmentVisible()
        }
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
                        override fun onAnimationStart(animation: Animation?) {}
                        override fun onAnimationRepeat(animation: Animation?) {}

                        override fun onAnimationEnd(animation: Animation?) {
                            onEnterAnimationEnded()
                        }
                    })
                }
            } catch (e: Exception) {
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
        (activity as? WhyGoogleActivity<*>)?.pop(this)
    }

    fun <P> popTo(target: Class<P>) {
        (activity as? WhyGoogleActivity<*>)?.popTo(target)
    }

    fun start(fragment: WhyGoogleFragment<*>, popAll: Boolean = false, stack: Boolean = true) {
        (activity as? WhyGoogleActivity<*>)?.start(fragment, popAll, stack)
    }

    fun startWithPop(fragment: WhyGoogleFragment<*>) {
        (activity as? WhyGoogleActivity<*>)?.startWithPop(fragment)
    }

    fun <P> startWithPopTo(fragment: WhyGoogleFragment<*>, target: Class<P>) {
        (activity as? WhyGoogleActivity<*>)?.startWithPopTo(fragment, target)
    }

    fun popAll() {
        (activity as? WhyGoogleActivity<*>)?.popAll()
    }
}