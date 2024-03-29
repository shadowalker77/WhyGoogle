package com.alirezabdn.whygoogle

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.alirezabdn.whygoogle.databinding.ActivityMainBinding
import ir.ayantech.whygoogle.activity.SwipableWhyGoogleActivity
import ir.ayantech.whygoogle.fragment.WhyGoogleFragment
import ir.ayantech.whygoogle.widget.DirectionCareSwipeBackContainer
import ir.ayantech.whygoogle.widget.RtlSwipeBackContainer
import ir.ayantech.whygoogle.widget.SwipeBackContainer

class MainActivity : SwipableWhyGoogleActivity<ActivityMainBinding>() {

    override val directionCareRtl: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null)
            start(MainFragment(), onFragmentCreationEndedCallback = {
                Log.d("WhG", "onFragmentCreationEndedCallback")
            })
    }

    override val binder: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    override val fragmentHost: SwipeBackContainer
        get() = binding.containerVp

    override fun onTopFragmentChanged(whyGoogleFragment: WhyGoogleFragment<*>) {
        super.onTopFragmentChanged(whyGoogleFragment)
        Log.d("WhG", "onTopFragmentChanged")
    }

//    override val containerId: Int
//        get() = R.id.containerFl
}