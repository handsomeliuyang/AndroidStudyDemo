package com.ly.lib.mvvm.domain.data

import com.ly.lib.mvvm.domain.Result
import com.ly.lib.mvvm.domain.entity.Character
import io.reactivex.Observable

interface CharacterDataSource {
    fun getCharacters(type: String) : Observable<Result<List<Character>>>
}