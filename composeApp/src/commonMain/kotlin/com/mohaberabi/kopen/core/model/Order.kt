package com.mohaberabi.kopen.core.model

data class Order(
    val id: String,
    val total: Double,
    val isSynced: Boolean
)