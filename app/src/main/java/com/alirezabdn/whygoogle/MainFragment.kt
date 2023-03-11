package com.alirezabdn.whygoogle

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alirezabdn.whygoogle.databinding.MainFragmentBinding
import ir.ayantech.whygoogle.databinding.FooterBinding
import ir.ayantech.whygoogle.dateTime.DateTime
import ir.ayantech.whygoogle.fragment.ViewBindingInflater
import ir.ayantech.whygoogle.fragment.WhyGoogleFragment
import ir.ayantech.whygoogle.helper.PreferencesManager
import ir.ayantech.whygoogle.standard.LaunchMode

class MainFragment : WhyGoogleFragment<MainFragmentBinding>() {

    override fun onCreate() {
        super.onCreate()
        accessViews {
            testTv.text = "tested now more than once"
            go.setOnClickListener {
                start(MainFragment())
            }
            go2.setOnClickListener {
                start(SecondFragment(), popAll = true, stack = false, LaunchMode.NORMAL)
            }
            binding.myVp2.adapter = object : FragmentStateAdapter(this@MainFragment) {
                override fun getItemCount(): Int = 4

                override fun createFragment(position: Int): Fragment = SimpleFragment()
            }
            binding.executeBtn.setOnClickListener {
                PreferencesManager.getInstance(requireContext()).apply {
                    val string = read<String>("string")
                    val boolean = read<Boolean>("boolean")
                    val float = read<Float>("float")
                    val int = read<Int>("int")
                    val long = read<Long>("long")

//                    oldSave("string", "string")
//                    oldSave("boolean", true)
//                    oldSave("float", 0.2f)
//                    oldSave("int", 12)
//                    oldSave("long", 155L)
                }
            }
            val dt = DateTime("2022-09-24T15:34:00")
            Log.d("dtt", dt.toString())
        }
    }

    override val footerInflater: ViewBindingInflater
        get() = FooterBinding::inflate

    override val defaultBackground: Int
        get() = R.color.teal_700

    override fun onBackToFragment() {
        super.onBackToFragment()
        Log.d("WhG", "backtofrag")
    }

    override fun onFragmentVisible() {
        super.onFragmentVisible()
        Log.d("WhG", "onFragmentVisible")
    }

    override fun onEnterAnimationEnded() {
        super.onEnterAnimationEnded()
        Log.d("WhG", "onEnterAnimationEnded")
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> MainFragmentBinding
        get() = MainFragmentBinding::inflate
}