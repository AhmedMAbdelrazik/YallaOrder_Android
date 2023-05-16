package com.isoft.yallaorder.data.models

sealed class Response<T>{
    data class SUCCESS<T>(val data:T?=null):Response<T>()
    data class ERROR<T>(val code:Int,val message:String):Response<T>()
    data class LOADING<T>(val isShowLoading:Boolean):Response<T>()
}
