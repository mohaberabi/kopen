package com.mohaberabi.kopen.core.domain

import com.mohaberabi.kopen.core.data.logger.SyncLogger
import com.mohaberabi.kopen.core.data.repository.OrderRepository

final class SyncOrdersUseCase(
    private val repository: OrderRepository,
    private val logger: SyncLogger
) {

    suspend operator fun invoke(): Result<Unit> {
        logger.logSyncStarted()
        val result = repository.sync()
        result.fold(
            onSuccess = {
                logger.logSyncSuccess(count = -1)
            },
            onFailure = {
                logger.logSyncFailure(it.message ?: "unknown")
            }
        )
        return result
    }
}