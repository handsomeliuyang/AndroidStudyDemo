package com.ly.lib.mvvm.ui.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.BindingBuildInfo
import androidx.databinding.DataBindingUtil
import com.ly.lib.mvvm.R
import com.ly.lib.mvvm.databinding.ActivityDetailBinding
import com.ly.lib.mvvm.ui.HouseType

class DetailActivity : AppCompatActivity() {

    companion object {
        private const val KEY_HOUSE = "house"
        fun startActivityWithTransition(
            activity: Activity,
            imageView: ImageView,
            type: HouseType
        ){
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra(KEY_HOUSE, type)

            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity, imageView, imageView.transitionName
            ).toBundle()
            activity.startActivity(intent, bundle)
        }
    }

    private val binding: ActivityDetailBinding by lazy {
        DataBindingUtil.setContentView<ActivityDetailBinding>(this, R.layout.activity_detail)
    }
    private val house: HouseType by lazy {
        this.intent.getSerializableExtra(KEY_HOUSE) as HouseType
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            house = this@DetailActivity.house
            lifecycleOwner = this@DetailActivity
        }
    }
}