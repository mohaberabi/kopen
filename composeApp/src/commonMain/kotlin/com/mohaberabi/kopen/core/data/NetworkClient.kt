package com.mohaberabi.kopen.core.data

import com.mohaberabi.kopen.core.model.OrderDto
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

final class NetworkClient(
    private val baseUrl: String
) {

    suspend fun getOrders(): List<OrderDto> {
        delay(5.seconds)
        return emptyList()
    }
}
