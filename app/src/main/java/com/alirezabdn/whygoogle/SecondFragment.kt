package com.alirezabdn.whygoogle

import android.view.LayoutInflater
import android.view.ViewGroup
import com.alirezabdn.whygoogle.databinding.MainFragmentBinding
import ir.ayantech.whygoogle.fragment.WhyGoogleFragment
import ir.ayantech.whygoogle.helper.makeGone

class SecondFragment : WhyGoogleFragment<MainFragmentBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> MainFragmentBinding
        get() = MainFragmentBinding::inflate

    override fun onCreate() {
        super.onCreate()
        binding.go2.makeGone()
    }
}