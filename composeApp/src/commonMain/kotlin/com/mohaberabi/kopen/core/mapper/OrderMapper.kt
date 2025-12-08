package com.mohaberabi.kopen.core.mapper

import com.mohaberabi.kopen.core.model.Order
import com.mohaberabi.kopen.core.model.OrderDto
import com.mohaberabi.kopen.core.model.OrderEntity

final class OrderMapper {

    fun dtoToEntity(dto: OrderDto, syncedAtMillis: Long?): OrderEntity {
        return OrderEntity(
            id = dto.id,
            total = dto.total,
            status = dto.status,
            syncedAtMillis = syncedAtMillis
        )
    }

    fun entityToDomain(entity: OrderEntity): Order {
        return Order(
            id = entity.id,
            total = entity.total,
            isSynced = entity.syncedAtMillis != null
        )
    }
}