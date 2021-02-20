package com.ly.lib.mvvm.data.api

import com.ly.lib.mvvm.model.Character

interface RemoteDataSource {

    fun getCharacters(type: String) : List<Character>

}