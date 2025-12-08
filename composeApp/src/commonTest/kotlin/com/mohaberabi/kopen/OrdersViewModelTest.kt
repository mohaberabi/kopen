package com.mohaberabi.kopen

import com.mohaberabi.kopen.core.data.repository.OrderRepository
import com.mohaberabi.kopen.core.domain.SyncOrdersUseCase
import com.mohaberabi.kopen.core.model.Order
import com.mohaberabi.kopen.core.presentation.OrdersUiState
import com.mohaberabi.kopen.core.presentation.OrdersViewModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class OrdersViewModelTest {

    @Test
    fun `viewModel emits Loaded when repository returns orders`() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val repository = mock<OrderRepository>()
        val syncUseCase = mock<SyncOrdersUseCase>()
        val orders = listOf(
            Order(id = "1", total = 10.0, isSynced = true),
            Order(id = "2", total = 20.0, isSynced = false)
        )
        val flow = MutableStateFlow(orders)
        every { repository.observeOrders() } returns flow
        everySuspend { syncUseCase.invoke() } returns Result.success(Unit)

        val vm = OrdersViewModel(
            repository = repository,
            syncUseCase = syncUseCase,
            dispatcher = dispatcher,
            externalScope = backgroundScope,
        )
        val state = vm.state.value
        assertTrue(state is OrdersUiState.Loaded)
        assertEquals(2, state.orders.size)
        verify { repository.observeOrders() }
    }

    @Test
    fun `refresh triggers sync and sets Error on failure`() = runTest {
        val dispatcher = UnconfinedTestDispatcher(testScheduler)
        val repository = mock<OrderRepository>()
        val syncUseCase = mock<SyncOrdersUseCase>()
        val emptyFlow = MutableStateFlow(emptyList<Order>())
        every { repository.observeOrders() } returns emptyFlow
        everySuspend { syncUseCase.invoke() } returns Result.failure(Exception("boom"))
        val vm = OrdersViewModel(
            repository = repository,
            syncUseCase = syncUseCase,
            dispatcher = dispatcher,
            externalScope = backgroundScope,
        )

        vm.refresh()
        verifySuspend { syncUseCase.invoke() }
        val state = vm.state.value
        assertTrue(state is OrdersUiState.Error)
        assertEquals("boom", state.message)
    }
}