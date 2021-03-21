package com.ly.lib.mvvm.presentation.detail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ly.lib.mvvm.domain.usecase.Result
import com.ly.lib.mvvm.domain.entity.Character
import com.ly.lib.mvvm.presentation.HouseType
import com.ly.lib.mvvm.domain.usecase.CharacterUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class DetailViewModel(
    private val house: HouseType,
    private val characterUseCase: CharacterUseCase
) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val characterList: MutableLiveData<List<Character>> = MutableLiveData<List<Character>>()
    val isUpdating = MutableLiveData<Boolean>()

    fun loadCharacterList() {
        Log.d("liuyang", "loadCharacterList")

        isLoading.postValue(true)
        characterUseCase.execute(
            CharacterUseCase.RequestValues(house.name),
            object : DisposableObserver<Result<List<Character>>>() {
                override fun onComplete() {
                    Log.d("liuyang", "onComplete")
                }

                override fun onNext(t: Result<List<Character>>) {
                    when (t) {
                        is Result.Success -> characterList.value = t.data
                        is Result.Error -> characterList.value = emptyList()
                    }
                    isLoading.value = false
//
                    Log.d("liuyang", t.toString())
                }

                override fun onError(e: Throwable) {
                    Log.e("liuyang", "onError", e)
                }
            })
    }

    fun updateCharacterList() {
        isUpdating.postValue(true)
//        val subscription = characterU
    }

    override fun onCleared() {
        super.onCleared()
        characterUseCase.dispose()
    }
}