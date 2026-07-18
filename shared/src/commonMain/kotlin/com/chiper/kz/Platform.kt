package com.chiper.kz

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform