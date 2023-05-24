package com.isoft.yallaorder

import android.accounts.Account
import android.content.Context
import android.util.Log
import android.util.SparseArray
import androidx.compose.ui.platform.LocalContext
import androidx.work.*
import com.google.android.gms.auth.GoogleAuthUtil
import com.isoft.yallaorder.background.GetDataFromGoogleSheetsWorker
import com.isoft.yallaorder.data.models.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

object Utils {
    fun fireWorker(context:Context):UUID{
        val getDataFromGoogleSheetsWorkRequest: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<GetDataFromGoogleSheetsWorker>()
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .build()
        val workManager =  WorkManager
            .getInstance(context.applicationContext)
        workManager.enqueueUniqueWork(Constants.GET_DATA_FROM_GOOGLE_SHEETS_WORKER,
            ExistingWorkPolicy.REPLACE,getDataFromGoogleSheetsWorkRequest)
        return getDataFromGoogleSheetsWorkRequest.id
    }

    fun fireWorkerWithInitialDelay(context:Context):UUID{
        val getDataFromGoogleSheetsWorkRequest: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<GetDataFromGoogleSheetsWorker>()
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .setInitialDelay(30,TimeUnit.SECONDS)
                .build()
        val workManager =  WorkManager
            .getInstance(context.applicationContext)
        workManager.enqueueUniqueWork(Constants.GET_DATA_FROM_GOOGLE_SHEETS_WORKER,
            ExistingWorkPolicy.REPLACE,getDataFromGoogleSheetsWorkRequest)
        return getDataFromGoogleSheetsWorkRequest.id
    }

    fun getRestaurantsList(googleSheetGetData: GoogleSheetGetData):List<Restaurant>{
        val restaurantsList = arrayListOf<Restaurant>()
        for (restaurant in googleSheetGetData.valueRanges[0].values){
            if(restaurant[5]=="TRUE"){
                 restaurantsList.add(
                     Restaurant(restaurant[0],restaurant[2],restaurant[1],restaurant[3],restaurant[4])
                 )
            }
        }
        return restaurantsList
    }

    fun convertOrderToGoogleSheetPostDataAndOrderTables(order:Order,user: User,restaurant: Restaurant):GoogleSheetPostDataAndOrderTables{
        val values = ArrayList<ArrayList<String>>()
        val orderTables = ArrayList<OrderTable>()
        val orderNumber = (1..1000000).random()
        val menuItemsSize = order.menuItems.size()
        val date = java.text.SimpleDateFormat(
            "dd-MM-yyyy", Locale("en")
        ).format(Date())

        for (i in 0 until menuItemsSize) {
            val orderArray = ArrayList<String>()
            orderArray.add(orderNumber.toString())
            orderArray.add(restaurant.name)
            orderArray.add(restaurant.id)
            order.menuItems.valueAt(i).apply {
                orderArray.add(name)
                orderArray.add(count.toString())
                orderArray.add(price.toString())
                orderTables.add(OrderTable(orderNo = orderNumber.toString(),
                    restaurantName = restaurant.name,
                    restaurantId = restaurant.id, menuItemName = name, orderCount = count.toString(),
                    menuItemPrice = price, orderTime = date, orderStatus = Constants.STATUS_PENDING))
            }
            orderArray.add(date)
            orderArray.add(user.mobileNumber!!)
            orderArray.add(user.fullName)
            orderArray.add(Constants.VALID)
            values.add(orderArray)

        }
        return GoogleSheetPostDataAndOrderTables(GoogleSheetPostData(values),orderTables)
    }

    suspend fun getToken(context: Context,oldAccessToken:String,accountName:String,accountType: String,
                         scope:String):String{
        GoogleAuthUtil.clearToken(context,oldAccessToken)
        return GoogleAuthUtil.getToken(context, Account(accountName,accountType),scope)
    }

    suspend fun getToken(context: Context,account: Account,scope:String):String{
        return GoogleAuthUtil.getToken(context, account,scope)
    }

    fun convertOrderTablesToOrder(orderTables: List<OrderTable>):Order{
        val menuItems = SparseArray<MenuItemData>()
        var totalPrice = 0.0f
        for (orderTable in orderTables){
            totalPrice+=orderTable.orderCount.toInt() * orderTable.menuItemPrice
            menuItems.put(orderTable.id, MenuItemData(orderTable.id.toString(),"",orderTable.menuItemName,orderTable.menuItemPrice,orderTable.orderCount.toInt()))
        }
        return Order(menuItems, totalPrice,orderTables[0].orderTime,orderTables[0].restaurantName,orderTables[0].orderStatus,orderTables[0].orderNo)
    }

    fun filterMenuItems(menuItems:List<MenuItemData>,query:String):List<MenuItemData>{
        if(query.isEmpty()){
            return menuItems
        }
        return menuItems.filter { it.name.contains(query,true) }
    }

    fun isEqualCurrentDate(date:String):Boolean{
        val currentDate = getCurrentDate()
        return date == currentDate
    }

    fun getCurrentDate():String{
        return  java.text.SimpleDateFormat(
            "dd-MM-yyyy", Locale("en")
        ).format(Date())
    }

    fun getSelectedOrderRanges(googleSheetGetData: GoogleSheetGetData,orderId:String): Pair<String, GoogleSheetPostData>? {
        Log.i("orderId",orderId)
        val rangesNumbersList = ArrayList<Int>()
        for (i in 0 until googleSheetGetData.valueRanges[0].values.size){
            if(googleSheetGetData.valueRanges[0].values[i][0]==orderId){
                rangesNumbersList.add(i+2)
            }
        }
        if(rangesNumbersList.isNotEmpty()) {
            val ordersRanges = "${Constants.ORDERS}J${rangesNumbersList[0]}:J${rangesNumbersList.last()}"
            Log.i("range",ordersRanges)
            val values = ArrayList<ArrayList<String>>()
            for (i in 0 until rangesNumbersList.size){
                val valueArray = ArrayList<String>()
                valueArray.add(Constants.REMOVED)
                values.add(valueArray)
            }
            return Pair(ordersRanges, GoogleSheetPostData(values))
        }
        return null
    }

}