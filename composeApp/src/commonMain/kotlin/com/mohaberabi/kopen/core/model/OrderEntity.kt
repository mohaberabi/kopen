package com.mohaberabi.kopen.core.model

data class OrderEntity(
    val id: String,
    val total: Double,
    val status: String,
    val syncedAtMillis: Long?
)
