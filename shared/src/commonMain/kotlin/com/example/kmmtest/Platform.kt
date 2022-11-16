package com.example.kmmtest

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform