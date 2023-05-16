package com.isoft.yallaorder.data.models

data class GoogleSheetStatusWithMenu(val status:String,
                                     val allRestaurants:List<Restaurant>?=null,
                                     var menuList:List<MenuItemData>?=null,
                                     val selectedRestaurant: Restaurant?=null)
