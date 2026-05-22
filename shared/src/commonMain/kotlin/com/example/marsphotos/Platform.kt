package com.example.marsphotos // Asegúrate de usar tu package real

interface Platform {
    val name: String
}


expect fun getPlatform(): Platform