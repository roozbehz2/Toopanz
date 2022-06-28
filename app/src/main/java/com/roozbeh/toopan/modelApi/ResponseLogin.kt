package com.roozbeh.toopan.modelApi

data class ResponseLogin(
    val refToken: String,
    val token: String,
    val user: User
)
