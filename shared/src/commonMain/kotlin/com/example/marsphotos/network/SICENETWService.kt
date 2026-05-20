package com.example.marsphotos.network

// Quitamos el import de Compose y dejamos puros de Ktor
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.* // De aquí sale el ContentType correcto

class SICENETWService(private val client: HttpClient) {

    private val baseUrl = "http://sicenet.itsur.edu.mx"

    suspend fun login(usuario: String, contrasenia: String): String {
        val response: HttpResponse = client.post("$baseUrl/login") {
            // Ahora sí usará el ContentType de Ktor sin marcar error
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("txtUsuario=$usuario&txtContrasenia=$contrasenia")
        }
        return response.bodyAsText()
    }

    suspend fun getPerfil(): String {
        val response: HttpResponse = client.get("$baseUrl/perfil.aspx")
        return response.bodyAsText()
    }

    suspend fun getCargaAcademica(): String {
        val response: HttpResponse = client.get("$baseUrl/carga_academica.aspx")
        return response.bodyAsText()
    }
}