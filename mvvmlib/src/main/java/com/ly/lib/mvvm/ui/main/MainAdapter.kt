package com.ly.lib.mvvm.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.ly.lib.mvvm.R
import com.ly.lib.mvvm.databinding.ItemHouseBinding
import com.ly.lib.mvvm.ui.HouseType

class MainAdapter(val action: (ImageView, HouseType) -> Unit) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    private val items = mutableListOf(
        HouseType.Gryffindor,
        HouseType.Hufflepuff,
        HouseType.Ravenclaw,
        HouseType.Slytherin
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val binding:ItemHouseBinding = DataBindingUtil.inflate<ItemHouseBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_house,
            parent,
            false
        )
        return MainViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class MainViewHolder(private val binding: ItemHouseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HouseType){
            binding.apply {
                house = item
                executePendingBindings()
                root.setOnClickListener {
                    action(binding.imageViewItemHouseLogo, item)
                }
            }
        }

    }
}