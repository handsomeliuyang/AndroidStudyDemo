package com.ly.lib.mvvm.domain.repository

import com.ly.lib.mvvm.domain.entity.Character
import com.ly.lib.mvvm.domain.usecase.Result
import io.reactivex.Observable

interface CharacterRepository {
    fun getCharacters(type: String) : Observable<Result<List<Character>>>
}