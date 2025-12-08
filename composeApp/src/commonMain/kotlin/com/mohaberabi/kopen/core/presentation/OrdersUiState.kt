package com.mohaberabi.kopen.core.presentation

import com.mohaberabi.kopen.core.data.repository.OrderRepository
import com.mohaberabi.kopen.core.domain.SyncOrdersUseCase
import com.mohaberabi.kopen.core.model.Order
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class OrdersUiState {
    data object Idle : OrdersUiState()
    data object Loading : OrdersUiState()
    data class Loaded(val orders: List<Order>) : OrdersUiState()
    data class Error(val message: String) : OrdersUiState()
}

final class OrdersViewModel(
    private val repository: OrderRepository,
    private val syncUseCase: SyncOrdersUseCase,
    private val dispatcher: CoroutineDispatcher,
    private val externalScope: CoroutineScope
) {

    private val _state = MutableStateFlow<OrdersUiState>(OrdersUiState.Idle)
    val state: StateFlow<OrdersUiState> = _state.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    init {
        observeOrders()
    }

    private fun observeOrders() {
        externalScope.launch(dispatcher) {
            repository.observeOrders().collect { orders ->
                if (orders.isNotEmpty()) {
                    _state.value = OrdersUiState.Loaded(orders)
                } else if (_state.value is OrdersUiState.Idle) {
                    _state.value = OrdersUiState.Loading
                }
            }
        }
    }

    fun refresh() {
        if (_isSyncing.value) return
        _isSyncing.value = true

        externalScope.launch(dispatcher) {
            val result = syncUseCase.invoke()
            _isSyncing.value = false

            result.exceptionOrNull()?.let {
                _state.value = OrdersUiState.Error(it.message ?: "unknown")
            }
        }
    }
}