package com.alirezabdn.whygoogle

import android.os.Bundle
import android.view.LayoutInflater
import com.alirezabdn.whygoogle.databinding.ActivityMainBinding
import ir.ayantech.whygoogle.activity.SwipableWhyGoogleActivity
import ir.ayantech.whygoogle.widget.SwipeBackContainer

class MainActivity : SwipableWhyGoogleActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        start(MainFragment())
    }

    override val binder: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    override val fragmentHost: SwipeBackContainer
        get() = binding.containerVp

//    override val containerId: Int
//        get() = R.id.containerFl
}