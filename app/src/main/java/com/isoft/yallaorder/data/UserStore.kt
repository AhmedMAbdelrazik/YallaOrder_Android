package com.isoft.yallaorder.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.isoft.yallaorder.data.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.*

class UserStore(private val context: Context) {
    companion object{
        private val Context.dataStore:DataStore<Preferences> by preferencesDataStore("user")
        private val FULL_NAME_KEY = stringPreferencesKey("user_name")
        private val GIVEN_NAME_KEY = stringPreferencesKey("given_name")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val PHOTO_URL_KEY = stringPreferencesKey("photo_url")
        private val MOBILE_NUMBER_KEY = stringPreferencesKey("mobile_number")
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val WORKER_INFO_UUID_KEY = stringPreferencesKey("work_info_UUID")
        private val IS_ORDER_CONFIRMED_KEY = booleanPreferencesKey("is_order_confirmed")
        private val ACCOUNT_NAME = stringPreferencesKey("account_name")
        private val ACCOUNT_TYPE = stringPreferencesKey("account_type")
    }

    val getUser: Flow<User?> = context.dataStore.data.map { preferences ->
        if(preferences[FULL_NAME_KEY]==null){
            null
        }else{
            User(preferences[FULL_NAME_KEY]!!,
                preferences[GIVEN_NAME_KEY]!!,
                preferences[EMAIL_KEY]!!,
                preferences[PHOTO_URL_KEY]!!,
                preferences[MOBILE_NUMBER_KEY],
                preferences[ACCESS_TOKEN_KEY]
            )
        }
    }

    suspend fun saveUser(user: User) {
        context.dataStore.edit { preferences ->
            preferences[FULL_NAME_KEY] = user.fullName
            preferences[GIVEN_NAME_KEY] = user.givenName
            preferences[EMAIL_KEY] = user.email
            preferences[PHOTO_URL_KEY]=user.photoUrl
            preferences[MOBILE_NUMBER_KEY] = user.mobileNumber!!
            preferences[ACCESS_TOKEN_KEY] = user.accessToken!!
            preferences[ACCOUNT_NAME] = user.accountName!!
            preferences[ACCOUNT_TYPE] = user.accountType!!
        }
    }

    suspend fun saveAccessToken(accessToken:String){
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
        }
    }

    suspend fun getAccessToken():String{
        return context.dataStore.data.map{
            it[ACCESS_TOKEN_KEY]
        }.first()!!
    }

    suspend fun getAccountName():String{
        return context.dataStore.data.map{
            it[ACCOUNT_NAME]
        }.first()!!
    }

    suspend fun getAccountType():String{
        return context.dataStore.data.map{
            it[ACCOUNT_TYPE]
        }.first()!!
    }

    suspend fun saveWorkerInfoUUID(id:UUID){
        context.dataStore.edit { preferences ->
            preferences[WORKER_INFO_UUID_KEY] = id.toString()
        }
    }

     fun getWorkerIndoUUID(): Flow<UUID?> =
        context.dataStore.data.map { preferences ->
            if(preferences[WORKER_INFO_UUID_KEY]==null){
                null
            }else {
                UUID.fromString(preferences[WORKER_INFO_UUID_KEY])
            }
        }

     suspend fun saveIsOrderConfirmed(isOrderConfirmed:Boolean){
         context.dataStore.edit { preferences ->
             preferences[IS_ORDER_CONFIRMED_KEY] = isOrderConfirmed
         }
     }
      fun getIsOrderConfirmed():Flow<Boolean> {
         return context.dataStore.data.map { preferences ->
             if (preferences[IS_ORDER_CONFIRMED_KEY] == null) {
                 false
             } else {
                 preferences[IS_ORDER_CONFIRMED_KEY]!!
             }
         }
     }
}