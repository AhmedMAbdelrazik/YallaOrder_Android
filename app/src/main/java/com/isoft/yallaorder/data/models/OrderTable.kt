package com.isoft.yallaorder.data.models

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OrderTable(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id: Int=0,
    var orderNo:String,
    var restaurantName:String,
    var restaurantId:String,
    var menuItemName:String,
    var orderCount:String,
    var menuItemPrice:Float,
    var orderTime:String,
    var orderStatus:String
)
