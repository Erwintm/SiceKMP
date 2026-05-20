package com.example.marsphotos // Asegúrate de usar tu package real

interface Platform {
    val name: String
}

// El contrato que cada plataforma debe cumplir obligatoriamente
expect fun getPlatform(): Platform