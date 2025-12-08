package com.mohaberabi.kopen.core.data.source

import com.mohaberabi.kopen.core.data.NetworkClient
import com.mohaberabi.kopen.core.model.OrderDto

final class OrderRemoteDataSource(
    private val client: NetworkClient
) {

    suspend fun fetchOrders(): List<OrderDto> {
        return client.getOrders()
    }
}