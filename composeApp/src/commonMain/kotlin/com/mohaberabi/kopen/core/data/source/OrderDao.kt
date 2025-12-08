package com.mohaberabi.kopen.core.data.source

import com.mohaberabi.kopen.core.model.OrderEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

final class OrderDao {

    private val orders = MutableStateFlow<List<OrderEntity>>(emptyList())

    fun observeAll(): Flow<List<OrderEntity>> = orders

    suspend fun replaceAll(newOrders: List<OrderEntity>) {
        orders.value = newOrders
    }

    suspend fun clear() {
        orders.value = emptyList()
    }
}