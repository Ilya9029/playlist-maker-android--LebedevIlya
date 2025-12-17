package com.example.new_project.domain

open class BaseResponse(
    open var resultCode: Int = 0,
    open var errorMessage: String? = null
)