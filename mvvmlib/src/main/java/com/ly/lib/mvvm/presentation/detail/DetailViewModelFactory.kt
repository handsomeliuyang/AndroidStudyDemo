package com.ly.lib.mvvm.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ly.lib.mvvm.data.repository.CharacterDataRepository
import com.ly.lib.mvvm.presentation.HouseType
import com.ly.lib.mvvm.domain.usecase.CharacterUseCase
import com.ly.lib.mvvm.presentation.executor.JobExecutor
import com.ly.lib.mvvm.presentation.executor.UIThread
import io.reactivex.schedulers.Schedulers

class DetailViewModelFactory(private val houseType: HouseType) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(houseType,
                CharacterUseCase(
                    CharacterDataRepository.INSTANCE,
                    JobExecutor.INSTANCE,
                    UIThread.INSTANCE
                )
            ) as T
        }
        throw RuntimeException("unknown class :" + modelClass.name)
    }

}