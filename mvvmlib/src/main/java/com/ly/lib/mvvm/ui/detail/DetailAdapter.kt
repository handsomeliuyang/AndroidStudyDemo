package com.ly.lib.mvvm.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ly.lib.mvvm.R
import com.ly.lib.mvvm.databinding.ItemCharacterBinding
import com.ly.lib.mvvm.model.Character

class DetailAdapter : RecyclerView.Adapter<DetailAdapter.DetailViewHolder>() {

    var itemList = listOf<Character>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val binding = DataBindingUtil.inflate<ItemCharacterBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_character,
            parent,
            false
        )

        return DetailViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    inner class DetailViewHolder(private val binding: ItemCharacterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Character){
            binding.apply {
                character = item
                executePendingBindings()
                root.setOnClickListener {
                    // ...
                }
            }
        }
    }
}