package com.alirezabdn.whygoogle

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.alirezabdn.whygoogle.databinding.MainFragmentBinding
import ir.ayantech.whygoogle.fragment.WhyGoogleFragment
import ir.ayantech.whygoogle.helper.fragmentArgument
import ir.ayantech.whygoogle.helper.makeGone

class SecondFragment : WhyGoogleFragment<MainFragmentBinding>() {

    var name: String? by fragmentArgument()
    override fun onCreate() {
        super.onCreate()
        binding.go2.makeGone()
        binding.go.setOnClickListener {
            pop()
        }
        binding.executeBtn.setOnClickListener {
            preventFromPop = false
        }
        binding.executeBtn.text = name
    }

    override var lockedSwipe: Boolean = true

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> MainFragmentBinding
        get() = MainFragmentBinding::inflate

    override fun onBackPressed(): Boolean {
        Toast.makeText(requireContext(), "back", Toast.LENGTH_SHORT).show()
        return super.onBackPressed()
    }
}