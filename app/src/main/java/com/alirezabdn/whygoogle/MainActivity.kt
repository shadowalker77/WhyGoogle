package com.alirezabdn.whygoogle

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewpager2.widget.ViewPager2
import com.alirezabdn.whygoogle.databinding.ActivityMainBinding
import ir.ayantech.whygoogle.activity.SwipableWhyGoogleActivity

class MainActivity : SwipableWhyGoogleActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lazyStart(MainFragment())
    }

    override val binder: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    override val fragmentHost: ViewPager2
        get() = binding.containerVp

//    override val containerId: Int
//        get() = R.id.containerFl
}