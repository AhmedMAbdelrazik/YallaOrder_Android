package com.isoft.yallaorder.data.models

data class MenuItemData(
    val id:String,
    val imageUrl:String,
    val name:String,
    val price:Float,
    var count:Int=0
)
