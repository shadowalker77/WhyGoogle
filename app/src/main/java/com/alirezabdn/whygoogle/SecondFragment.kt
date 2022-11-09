package com.alirezabdn.whygoogle

import android.view.LayoutInflater
import android.view.ViewGroup
import com.alirezabdn.whygoogle.databinding.MainFragmentBinding
import ir.ayantech.whygoogle.fragment.WhyGoogleFragment
import ir.ayantech.whygoogle.helper.makeGone
import ir.ayantech.whygoogle.standard.LaunchMode

class SecondFragment : WhyGoogleFragment<MainFragmentBinding>() {

    override fun onCreate() {
        super.onCreate()
        binding.go2.makeGone()
        binding.go.setOnClickListener {
            start(MainFragment(), popAll = true, stack = false, LaunchMode.NORMAL)
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> MainFragmentBinding
        get() = MainFragmentBinding::inflate
}