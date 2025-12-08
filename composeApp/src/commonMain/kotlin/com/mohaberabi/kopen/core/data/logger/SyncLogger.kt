package com.mohaberabi.kopen.core.data.logger

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

final class SyncLogger {

    private val _events = MutableSharedFlow<String>(replay = 0)
    val events: SharedFlow<String> = _events.asSharedFlow()

    suspend fun logSyncStarted() {
        _events.emit("sync_started")
    }

    suspend fun logSyncSuccess(count: Int) {
        _events.emit("sync_success_$count")
    }

    suspend fun logSyncFailure(message: String) {
        _events.emit("sync_failure_$message")
    }
}
