package com.ly.lib.mvvm.ui.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
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
    private val viewModel: DetailViewModel by lazy {
        ViewModelProvider(this, DetailViewModelFactory(house)).get(DetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            house = this@DetailActivity.house
            lifecycleOwner = this@DetailActivity
            viewModel = this@DetailActivity.viewModel
        }

        viewModel.loadCharacterList()
    }
}