package com.ly.lib.mvvm.ui.detail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ly.lib.mvvm.domain.Result
import com.ly.lib.mvvm.domain.entity.Character
import com.ly.lib.mvvm.ui.HouseType
import com.ly.lib.mvvm.ui.detail.domain.CharacterUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DetailViewModel(private val house: HouseType, private val characterUseCase: CharacterUseCase) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val characterList : MutableLiveData<List<Character>> = MutableLiveData<List<Character>>()
    val isUpdating = MutableLiveData<Boolean>()

    fun loadCharacterList() {
        Log.d("liuyang", "loadCharacterList")

        isLoading.postValue(true)
        val subscription = characterUseCase.execute(CharacterUseCase.RequestValues(house.name))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when (it) {
                    is Result.Success -> characterList.value = it.data
                    is Result.Error -> characterList.value = emptyList()
                }
                isLoading.value = false

                Log.d("liuyang", it.toString())
            }
    }

    fun updateCharacterList(){
        isUpdating.postValue(true)
//        val subscription = characterU
    }
}