package com.isoft.yallaorder

import com.google.android.gms.common.Scopes

object Constants {
    const val BASE_URL = "https://sheets.googleapis.com/v4/spreadsheets/"
    const val RESTURANTS_RANGE_VALUE = "Restaurants!A2:G"
    const val RANGES_KEY = "ranges"
    const val MAJOR_DIMENSION_KEY = "majorDimension"
    const val ROWS_VALUE = "ROWS"
    const val RESTURANTS_ID_RANGE_VALUE = "RestaurantID_"
    const val A2_G_RANGE = "!A2:G"
    const val ORDER_STATUS_RANGE_VALUE="OrderStatus!A2:C"
    const val ORDERS_RANGE_VALUE = "Orders!A2:I2"
    const val BEARER_VALUE = "Bearer"
    const val ERROR_MESSAGE_KEY = "errorMessage"
    const val STATUS_PENDING = "Pending"
    const val STATUS_ORDERED = "Ordered"
    const val STATUS_DELIVERED="Delivered"
    const val RAW_VALUE = "RAW"
    const val INSERT_ROWS_VALUE = "INSERT_ROWS"
    const val SUCCESS_MESSAGE_KEY="successMessage"
    const val GET_DATA_FROM_GOOGLE_SHEETS_WORKER="getDataFromGoogleSheetWorker"
    const val GET_GOOGLE_SHEET_DATA_LOCAL_BROADCAST = "get_google_sheet_data_local_broadcast"
    const val GET_GOOGLE_SHEET_MESSAGE = "message"
    const val GET_TOKEN_SCOPE = "oauth2:profile email" + " " + "https://www.googleapis.com/auth/spreadsheets" + " " + Scopes.DRIVE_FULL
    const val UN_AUTHORIZED_CODE = 401
}