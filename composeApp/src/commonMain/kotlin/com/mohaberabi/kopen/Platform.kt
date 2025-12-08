package com.mohaberabi.kopen

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform