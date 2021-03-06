package ir.ayantech.whygoogle.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewbinding.ViewBinding
import ir.ayantech.whygoogle.fragment.WhyGoogleFragment
import ir.ayantech.whygoogle.helper.viewBinding

abstract class WhyGoogleActivity<T : ViewBinding> : AppCompatActivity() {
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

    fun start(fragment: WhyGoogleFragment<*>, popAll: Boolean = false, stack: Boolean = true) {
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
            .addOrReplace(containerId, fragment)
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

    fun startWithPop(fragment: WhyGoogleFragment<*>) {
        pop()
        start(fragment)
    }

    fun <P> startWithPopTo(fragment: WhyGoogleFragment<*>, target: Class<P>) {
        popTo(target)
        start(fragment)
    }

    fun <P> popTo(target: Class<P>) {
        while (getTopFragment()?.javaClass?.name != target.name) pop()
    }

    fun pop(from: WhyGoogleFragment<*>? = null) {
        if (from != null && getTopFragment()?.javaClass?.simpleName != from.javaClass.simpleName)
            return
        supportFragmentManager.popBackStackImmediate()
        getTopFragment()?.let { onTopFragmentChanged(it) }
    }

    fun popAll() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        getTopFragment()?.let { onTopFragmentChanged(it) }
    }

    open fun onTopFragmentChanged(whyGoogleFragment: WhyGoogleFragment<*>) {
    }

    fun getTopFragment() =
        supportFragmentManager.findFragmentById(containerId) as? WhyGoogleFragment<*>?

    fun getFragmentCount() = supportFragmentManager.backStackEntryCount

    override fun onBackPressed() {
        if (getTopFragment()?.onBackPressed() == true)
            return
        super.onBackPressed()
    }
}