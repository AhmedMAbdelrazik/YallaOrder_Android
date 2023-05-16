package com.isoft.yallaorder.background

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.*
import com.google.gson.Gson
import com.isoft.yallaorder.Constants
import com.isoft.yallaorder.Utils
import com.isoft.yallaorder.data.UserStore
import com.isoft.yallaorder.data.models.GoogleSheetStatusWithMenu
import com.isoft.yallaorder.data.models.MenuItemData
import com.isoft.yallaorder.data.models.Restaurant
import com.isoft.yallaorder.data.netwrok.ApiClient
import retrofit2.HttpException


class GetDataFromGoogleSheetsWorker(
    appContext:Context,params:WorkerParameters):CoroutineWorker(appContext,params)
{
    private val apiClient = ApiClient()
    private val userStore = UserStore(appContext)
    override suspend fun doWork(): Result {
        return getGoogleSheetWithMenuData()
    }

    private suspend fun getGoogleSheetWithMenuData():Result{
        var result:Result
        try {
            val restaurantsPair = getAllRestaurantsAndSelectedOne()
            val googleSheets = apiClient.api.readSheet(Constants.ORDER_STATUS_RANGE_VALUE,
                Constants.ROWS_VALUE,userStore.getAccessToken())
            var googleSheetStatusWithMenu =
                GoogleSheetStatusWithMenu(Constants.STATUS_PENDING,
                    selectedRestaurant = restaurantsPair.first,
                    allRestaurants = restaurantsPair.second)
            for (status in googleSheets.valueRanges[0].values){
                if(status[2] == "TRUE"){
                    if(status[1] == Constants.STATUS_PENDING){
                        if(restaurantsPair.first!=null){
                            googleSheetStatusWithMenu = GoogleSheetStatusWithMenu(Constants.STATUS_PENDING,
                                restaurantsPair.second,
                                getRestaurantMenu(restaurantsPair.first!!.id),
                                restaurantsPair.first
                            )
                        }
                    }else if(status[1]==Constants.STATUS_ORDERED){
                        googleSheetStatusWithMenu = GoogleSheetStatusWithMenu(Constants.STATUS_ORDERED,
                            selectedRestaurant = restaurantsPair.first,
                        allRestaurants = restaurantsPair.second)
                    }else if(status[1]==Constants.STATUS_DELIVERED){
                        googleSheetStatusWithMenu = GoogleSheetStatusWithMenu(
                            Constants.STATUS_DELIVERED,
                            selectedRestaurant = restaurantsPair.first,
                            allRestaurants = restaurantsPair.second)
                    }
                }
            }
//            val outputData = Data.Builder()
//                .putString(Constants.SUCCESS_MESSAGE_KEY,Gson().toJson(googleSheetStatusWithMenu))
//                .build()

            result =  Result.success()
            val intent = Intent(Constants.GET_GOOGLE_SHEET_DATA_LOCAL_BROADCAST)
            // on below line we are passing data to our broad cast receiver with key and value pair.
            intent.putExtra(Constants.GET_GOOGLE_SHEET_MESSAGE, Gson().toJson(googleSheetStatusWithMenu))
            // on below line we are sending our broad cast with intent using broad cast manager.
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            Log.i("successStatus",googleSheetStatusWithMenu.status)
        }catch (e:Exception) {
            result = Result.failure()
            if (e is HttpException) {
                if (e.code() == Constants.UN_AUTHORIZED_CODE) {
                    Log.i("failureCode", e.code().toString())
                    userStore.saveAccessToken(
                        Utils.getToken(applicationContext,
                            userStore.getAccessToken(),
                            userStore.getAccountName(),
                            userStore.getAccountType(),
                            Constants.GET_TOKEN_SCOPE)
                    )
                    getGoogleSheetWithMenuData()
                }
            }
            Log.i("failure", e.localizedMessage)
        }
        Utils.fireWorkerWithInitialDelay(applicationContext)
        return result
    }

    private suspend fun getRestaurantMenu(id: String): List<MenuItemData> {
        val  googleSheets = apiClient.api.readSheet(
            Constants.RESTURANTS_ID_RANGE_VALUE+id+Constants.A2_G_RANGE,
            Constants.ROWS_VALUE,userStore.getAccessToken())
        val menuItemsData = arrayListOf<MenuItemData>()
        for (menuItem in googleSheets.valueRanges[0].values){
            if(menuItem[5]=="TRUE"){
                menuItemsData.add(MenuItemData(menuItem[1],menuItem[4],menuItem[2],menuItem[3].toFloat()))
            }
        }
        return menuItemsData
    }

    private suspend fun getAllRestaurantsAndSelectedOne():Pair<Restaurant?,List<Restaurant>?>{
        val googleSheets = apiClient.api.readSheet(Constants.RESTURANTS_RANGE_VALUE,
            Constants.ROWS_VALUE,userStore.getAccessToken())

        var selectedRestaurant:Restaurant? = null

        for (restaurant in googleSheets.valueRanges[0].values){
            if(restaurant[6]=="TRUE" && restaurant[5]=="TRUE"){
                selectedRestaurant = Restaurant(restaurant[0],restaurant[2],restaurant[1],restaurant[3],restaurant[4])
            }
        }
        return Pair(selectedRestaurant,Utils.getRestaurantsList(googleSheets))
    }

}