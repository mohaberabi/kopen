package com.mohaberabi.kopen.core.data.repository

import com.mohaberabi.kopen.core.data.source.OrderLocalDataSource
import com.mohaberabi.kopen.core.data.source.OrderRemoteDataSource
import com.mohaberabi.kopen.core.mapper.OrderMapper
import com.mohaberabi.kopen.core.model.Order
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock

final class OrderRepository(
    private val remote: OrderRemoteDataSource,
    private val local: OrderLocalDataSource,
    private val mapper: OrderMapper,
    private val ioDispatcher: CoroutineDispatcher
) {

    fun observeOrders(): Flow<List<Order>> {
        return local
            .observeOrders()
            .map { entities ->
                entities.map { mapper.entityToDomain(it) }
            }
    }

    suspend fun sync(): Result<Unit> {
        return withContext(ioDispatcher) {
            runCatching {
                val remoteOrders = remote.fetchOrders()
                val now = 1000000L
                val entities = remoteOrders.map { mapper.dtoToEntity(it, now) }
                local.clear()
                local.overwriteOrders(entities)
            }
        }
    }
}