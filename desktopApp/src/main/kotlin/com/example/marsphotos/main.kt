package com.example.marsphotos

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "SiceKMP",
    ) {
        App()
    }
}