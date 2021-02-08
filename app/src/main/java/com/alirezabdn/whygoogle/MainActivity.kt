package com.alirezabdn.whygoogle

import android.os.Bundle
import android.view.LayoutInflater
import com.alirezabdn.whygoogle.databinding.ActivityMainBinding
import ir.ayantech.whygoogle.activity.WhyGoogleActivity

class MainActivity : WhyGoogleActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        start(MainFragment())
    }

    override val binder: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    override val containerId: Int
        get() = R.id.containerFl
}