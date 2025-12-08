package com.mohaberabi.kopen

import com.mohaberabi.kopen.core.data.logger.SyncLogger
import com.mohaberabi.kopen.core.data.repository.OrderRepository
import com.mohaberabi.kopen.core.domain.SyncOrdersUseCase
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class SyncOrdersUseCaseTest {

    private val repository = mock<OrderRepository>()
    private val logger = mock<SyncLogger>()

    private val useCase = SyncOrdersUseCase(
        repository = repository,
        logger = logger
    )

    @Test
    fun `execute logs start and success on successful sync`() = runTest {
        everySuspend { repository.sync() } returns Result.success(Unit)
        everySuspend { logger.logSyncStarted() } returns Unit
        everySuspend { logger.logSyncSuccess(any()) } returns Unit
        val result = useCase.invoke()
        assertTrue(result.isSuccess)
        verifySuspend { logger.logSyncStarted() }
        verifySuspend { repository.sync() }
        verifySuspend { logger.logSyncSuccess(any()) }
    }

    @Test
    fun `execute logs failure when repository sync fails`() = runTest {
        val error = RuntimeException("network")
        everySuspend { repository.sync() } returns Result.failure(error)
        everySuspend { logger.logSyncStarted() } returns Unit
        everySuspend { logger.logSyncFailure(any()) } returns Unit
        val result = useCase.invoke()
        assertTrue(result.isFailure)
        verifySuspend { logger.logSyncStarted() }
        verifySuspend { repository.sync() }
        verifySuspend { logger.logSyncFailure("network") }
    }
}