package com.mohaberabi.kopen


import com.mohaberabi.kopen.core.data.repository.OrderRepository
import com.mohaberabi.kopen.core.data.source.OrderDao
import com.mohaberabi.kopen.core.data.source.OrderLocalDataSource
import com.mohaberabi.kopen.core.data.source.OrderRemoteDataSource
import com.mohaberabi.kopen.core.mapper.OrderMapper
import com.mohaberabi.kopen.core.model.OrderDto
import com.mohaberabi.kopen.core.model.OrderEntity
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class OrderRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private val testScope = TestScope(dispatcher)

    private val remote = mock<OrderRemoteDataSource>()
    private val local = mock<OrderLocalDataSource>()
    private val mapper = OrderMapper()

    @Test
    fun `sync should fetch remote orders and write to local`() = testScope.runTest {
        val dtoList = listOf(
            OrderDto(id = "1", total = 100.0, status = "NEW"),
            OrderDto(id = "2", total = 200.0, status = "PAID")
        )
        everySuspend { remote.fetchOrders() } returns dtoList
        everySuspend { local.clear() } returns Unit
        everySuspend { local.overwriteOrders(any()) } returns Unit
        val repo = OrderRepository(
            remote = remote,
            local = local,
            mapper = mapper,
            ioDispatcher = dispatcher
        )
        val result = repo.sync()
        assertTrue(result.isSuccess)
        verifySuspend() { remote.fetchOrders() }
        verifySuspend() { local.clear() }
        verifySuspend() { local.overwriteOrders(any()) }
    }

    @Test
    fun `observeOrders should map entities to domain`() = testScope.runTest {
        val dao = OrderDao()
        val localReal = OrderLocalDataSource(dao)
        val repo = OrderRepository(
            remote = remote,
            local = localReal,
            mapper = mapper,
            ioDispatcher = dispatcher
        )

        val entities = listOf(
            OrderEntity("1", 10.0, "NEW", syncedAtMillis = null),
            OrderEntity("2", 20.0, "NEW", syncedAtMillis = 1234L)
        )
        dao.replaceAll(entities)
        val result = repo.observeOrders().first()
        assertEquals(2, result.size)
        assertEquals(false, result[0].isSynced)
        assertEquals(true, result[1].isSynced)
    }

    @Test
    fun mohab() {
        assertTrue {
            true
        }
    }
}
