package com.isoft.yallaorder.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.isoft.yallaorder.data.models.OrderTable

@Dao
interface OrderDao {
    @Insert
    suspend fun addOrders(orders:List<OrderTable>)

    @Query("UPDATE OrderTable SET orderStatus=:orderStatus")
    suspend fun updateAllOrdersToStatus(orderStatus:String)

    @Query("SELECT DISTINCT orderNo from OrderTable ORDER BY id DESC")
    suspend fun getOrdersNo():List<String>

    @Query("SELECT * FROM OrderTable WHERE orderNo=:orderNo")
    suspend fun getOrders(orderNo:String):List<OrderTable>

}