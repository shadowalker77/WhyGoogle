package ir.ayantech.whygoogle.standard

import ir.ayantech.whygoogle.fragment.WhyGoogleFragment
import ir.ayantech.whygoogle.helper.SimpleCallBack

interface WhyGoogleInterface {
    fun start(
        fragment: WhyGoogleFragment<*>,
        popAll: Boolean = false,
        stack: Boolean = true,
        onFragmentCreationEndedCallback: SimpleCallBack? = null
    )

    fun startWithPop(fragment: WhyGoogleFragment<*>)

    fun <P> startWithPopTo(fragment: WhyGoogleFragment<*>, target: Class<P>)

    fun <P> popTo(target: Class<P>)

    fun pop()

    fun popAll()

    fun onTopFragmentChanged(whyGoogleFragment: WhyGoogleFragment<*>)

    fun getTopFragment(): WhyGoogleFragment<*>?

    fun getFragmentCount(): Int
}