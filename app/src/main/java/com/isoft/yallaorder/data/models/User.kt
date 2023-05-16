package com.isoft.yallaorder.data.models

data class User(
    val fullName:String,
    val givenName:String,
    val email:String,
    val photoUrl:String,
    var mobileNumber:String?=null,
    var accessToken:String?=null,
    var accountName:String?=null,
    var accountType:String?=null
)
