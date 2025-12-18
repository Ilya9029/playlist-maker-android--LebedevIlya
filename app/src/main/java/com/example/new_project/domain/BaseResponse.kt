package com.example.new_project.domain

open class BaseResponse(
    open val resultCode: Int = 0,
    open val errorMessage: String? = null
)