package com.isoft.yallaorder.data.models

import android.util.SparseArray

data class Order(val menuItems:SparseArray<MenuItemData>,
                 var totalPrice:Float,
                 var date:String="",
                 var restaurantName:String="",
                 var status:String="")
