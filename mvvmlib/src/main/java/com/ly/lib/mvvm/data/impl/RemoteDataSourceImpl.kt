package com.ly.lib.mvvm.data.impl

import com.ly.lib.mvvm.data.api.RemoteDataSource
import com.ly.lib.mvvm.model.Character

class RemoteDataSourceImpl : RemoteDataSource {

    override fun getCharacters(type: String): List<Character> {
        return NetworkModule.service
            .getCharacters(type)
            .execute()
            .body() ?: emptyList()
    }

}