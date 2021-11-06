package ir.ayantech.whygoogle.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewbinding.ViewBinding
import ir.ayantech.whygoogle.fragment.WhyGoogleFragment
import ir.ayantech.whygoogle.helper.SimpleCallBack
import ir.ayantech.whygoogle.helper.trying
import ir.ayantech.whygoogle.helper.viewBinding
import ir.ayantech.whygoogle.standard.LaunchMode
import ir.ayantech.whygoogle.standard.WhyGoogleInterface

abstract class WhyGoogleActivity<T : ViewBinding> : AppCompatActivity(), WhyGoogleInterface {
    abstract val containerId: Int

    val binding: T by viewBinding(binder)

    abstract val binder: (LayoutInflater) -> T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    fun accessViews(block: T.() -> Unit) {
        binding.apply {
            block()
        }
    }

    fun start(fragment: WhyGoogleFragment<*>) {
        start(fragment, false, true, LaunchMode.NORMAL, null)
    }

    override fun start(
        fragment: WhyGoogleFragment<*>,
        popAll: Boolean,
        stack: Boolean,
        launchMode: LaunchMode,
        onFragmentCreationEndedCallback: SimpleCallBack?
    ) {
        if (!stack) {
            try {
                if (getTopFragment()?.javaClass?.simpleName == fragment.javaClass.simpleName)
                    return
            } catch (e: Exception) {
            }
        }
        if (popAll) popAll()
        supportFragmentManager.beginTransaction()
            .also {
                if (fragment.getFragmentTransactionAnimation() != null) {
                    it.setCustomAnimations(
                        fragment.getFragmentTransactionAnimation()!!.fragmentEnter,
                        fragment.getFragmentTransactionAnimation()!!.fragmentExit,
                        fragment.getFragmentTransactionAnimation()!!.fragmentPopEnter,
                        fragment.getFragmentTransactionAnimation()!!.fragmentPopExit,
                    )
                }
            }
            .addOrReplace(containerId,
                if (launchMode == LaunchMode.SINGLE_TASK) getFragmentByClass(fragment.javaClass)
                    ?: fragment else fragment
            )
            .addToBackStack(fragment.javaClass.simpleName)
            .commitAllowingStateLoss()
        onTopFragmentChanged(fragment)
    }

    private fun FragmentTransaction.addOrReplace(
        containerId: Int,
        fragment: WhyGoogleFragment<*>
    ): FragmentTransaction {
        this.replace(containerId, fragment)
        return this
    }

    override fun startWithPop(fragment: WhyGoogleFragment<*>) {
        pop()
        start(fragment)
    }

    override fun <P> startWithPopTo(fragment: WhyGoogleFragment<*>, target: Class<P>) {
        popTo(target)
        start(fragment)
    }

    override fun <P> popTo(target: Class<P>) {
        while (getTopFragment()?.javaClass?.name != target.name) pop()
    }

    override fun pop() {
        supportFragmentManager.popBackStackImmediate()
        getTopFragment()?.let { onTopFragmentChanged(it) }
    }

    override fun popAll() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        getTopFragment()?.let { onTopFragmentChanged(it) }
    }

    override fun onTopFragmentChanged(whyGoogleFragment: WhyGoogleFragment<*>) {
    }

    override fun getTopFragment() =
        supportFragmentManager.findFragmentById(containerId) as? WhyGoogleFragment<*>?

    override fun getFragmentCount() = supportFragmentManager.backStackEntryCount

    override fun onBackPressed() {
        when {
            getTopFragment()?.onBackPressed() == true -> {
            }
            getFragmentCount() > 1 -> trying { pop() }
            else -> ActivityCompat.finishAfterTransition(this)
        }
    }

    override fun <T> getFragmentByClass(target: Class<T>): WhyGoogleFragment<*>? {
        return supportFragmentManager.findFragmentByTag(target.name) as? WhyGoogleFragment<*>
    }
}