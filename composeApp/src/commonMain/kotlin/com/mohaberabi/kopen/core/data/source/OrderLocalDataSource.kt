package com.mohaberabi.kopen.core.data.source

import com.mohaberabi.kopen.core.model.OrderEntity
import kotlinx.coroutines.flow.Flow

final class OrderLocalDataSource(
    private val dao: OrderDao
) {

    fun observeOrders(): Flow<List<OrderEntity>> = dao.observeAll()

    suspend fun overwriteOrders(orders: List<OrderEntity>) {
        dao.replaceAll(orders)
    }

    suspend fun clear() {
        dao.clear()
    }
}
