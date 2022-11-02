package com.alirezabdn.whygoogle

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alirezabdn.whygoogle.databinding.MainFragmentBinding
import ir.ayantech.whygoogle.dateTime.DateTime
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
                start(SecondFragment())
            }
            binding.myVp2.adapter = object : FragmentStateAdapter(this@MainFragment) {
                override fun getItemCount(): Int = 4

                override fun createFragment(position: Int): Fragment = SimpleFragment()
            }
            val dt = DateTime("2022-09-24T15:34:00")
            Log.d("dtt", dt.toString())
        }
    }

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