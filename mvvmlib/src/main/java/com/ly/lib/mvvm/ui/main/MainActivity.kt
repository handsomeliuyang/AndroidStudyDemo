package com.ly.lib.mvvm.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.ly.lib.mvvm.R
import com.ly.lib.mvvm.databinding.ActivityMainBinding
import com.ly.lib.mvvm.ui.detail.DetailActivity

class MainActivity: AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            lifecycleOwner = this@MainActivity
            adapter = MainAdapter { view, type ->
                DetailActivity.startActivityWithTransition(this@MainActivity, view, type)
            }
        }
    }

}