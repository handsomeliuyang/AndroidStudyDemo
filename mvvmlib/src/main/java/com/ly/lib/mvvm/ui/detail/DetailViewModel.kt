package com.ly.lib.mvvm.ui.detail

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ly.lib.mvvm.data.api.RemoteDataSource
import com.ly.lib.mvvm.model.Character
import com.ly.lib.mvvm.ui.HouseType

class DetailViewModel(private val house: HouseType, private val source: RemoteDataSource) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val characterList : MutableLiveData<List<Character>> = MutableLiveData<List<Character>>()

    fun loadCharacterList() {
        // 创建子线程，或者子协程
        Thread(Runnable {
            isLoading.postValue(true)
            characterList.postValue(source.getCharacters(house.name))
            isLoading.postValue(false)
        }).start()
    }
}