package com.example.new_project.creator

import com.example.new_project.data.RetrofitNetworkClient
import com.example.new_project.data.TracksRepositoryImpl
import com.example.new_project.domain.TracksRepository

object Creator {

    fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(
            RetrofitNetworkClient(
                Storage()
            )
        )
    }
}
