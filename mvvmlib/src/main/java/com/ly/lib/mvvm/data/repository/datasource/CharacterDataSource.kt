package com.ly.lib.mvvm.data.repository.datasource

import com.ly.lib.mvvm.domain.entity.Character
import com.ly.lib.mvvm.domain.usecase.Result
import io.reactivex.Observable

interface CharacterDataSource {
    fun getCharacters(type: String) : Observable<Result<List<Character>>>
}