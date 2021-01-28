package com.ly.lib.mvvm.ui.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.ly.lib.mvvm.R
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
            // TODO 添加过渡动画到详情页
//            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                activity, imageView, imageView.transitionName
//            )
//            activity.startActivity(intent, options.toBundle())
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
    }
}