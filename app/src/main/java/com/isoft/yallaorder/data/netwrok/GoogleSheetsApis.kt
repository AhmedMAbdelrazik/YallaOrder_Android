package com.isoft.yallaorder.data.netwrok

import com.isoft.yallaorder.data.models.GoogleSheetGetData
import com.isoft.yallaorder.data.models.GoogleSheetPostData
import retrofit2.http.*

interface GoogleSheetsApis {
    @GET("1J2rW5R_X3eKEHZHSv5Dix4zCKiVVnlgHlJ1H0F7gyAg/values:batchGet")
    suspend fun readSheet(@Query("ranges")ranges:String,
                          @Query("majorDimension") majorDimension:String,
                          @Query("access_token") accessToken:String):GoogleSheetGetData

    @POST("1J2rW5R_X3eKEHZHSv5Dix4zCKiVVnlgHlJ1H0F7gyAg/values/{ranges}:append")
    suspend fun submitSheet(@Path("ranges") ranges: String,
                            @Query("valueInputOption") valueInputOption:String,
                            @Query("insertDataOption") insertDataOption:String,
                            @Header("Authorization") bearerToken:String,
                            @Body data:GoogleSheetPostData)

    @PUT("1J2rW5R_X3eKEHZHSv5Dix4zCKiVVnlgHlJ1H0F7gyAg/values/{ranges}")
    suspend fun updateSheet(@Path("ranges") ranges: String,
                            @Query("valueInputOption") valueInputOption:String,
                            @Header("Authorization") bearerToken:String,
                            @Body data:GoogleSheetPostData)


}