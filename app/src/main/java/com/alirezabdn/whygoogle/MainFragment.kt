package com.alirezabdn.whygoogle

import android.view.LayoutInflater
import android.view.ViewGroup
import com.alirezabdn.whygoogle.databinding.MainFragmentBinding
import ir.ayantech.whygoogle.fragment.WhyGoogleFragment

class MainFragment : WhyGoogleFragment<MainFragmentBinding>() {

    override fun onCreate() {
        super.onCreate()
        accessViews {
            testTv.text = "tested now more than once"
            go.setOnClickListener {
                start(MainFragment())
            }
            go2.setOnClickListener {
                start(SecondFragment(), true)
            }
        }
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> MainFragmentBinding
        get() = MainFragmentBinding::inflate
}