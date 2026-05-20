package com.example.marsphotos

class WebPlatform : Platform {
    override val name: String = "Web Browser"
}

actual fun getPlatform(): Platform = WebPlatform()