package com.roozbeh.toopan.modelApi

data class ResponseLogin(
    var refToken: String,
    var token: String,
    var user: User
)
