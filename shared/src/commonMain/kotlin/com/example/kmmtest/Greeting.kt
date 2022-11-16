package com.example.kmmtest

class Greeting {
    private val platform: Platform = getPlatform()

    fun greeting(): String {
        return "Hello, ${platform.name}!这段文字是kotlin跨平台代码生成的。"
    }
}