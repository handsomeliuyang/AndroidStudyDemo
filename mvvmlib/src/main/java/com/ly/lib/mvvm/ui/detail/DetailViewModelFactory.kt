package com.ly.lib.mvvm.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ly.lib.mvvm.dataimpl.CharacterRepository
import com.ly.lib.mvvm.ui.HouseType
import com.ly.lib.mvvm.ui.detail.domain.CharacterUseCase

class DetailViewModelFactory(private val houseType: HouseType) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(houseType, CharacterUseCase(CharacterRepository.INSTANCE)) as T
        }
        throw RuntimeException("unknown class :" + modelClass.name)
    }

}