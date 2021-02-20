package com.ly.lib.mvvm.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ly.lib.mvvm.data.impl.RemoteDataSourceImpl
import com.ly.lib.mvvm.ui.HouseType

class DetailViewModelFactory(private val houseType: HouseType) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(houseType, RemoteDataSourceImpl()) as T
        }
        throw RuntimeException("unknown class :" + modelClass.name)
    }

}