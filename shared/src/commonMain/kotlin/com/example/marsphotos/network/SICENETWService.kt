package com.example.marsphotos.network

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class SICENETWService(private val client: HttpClient) {

    // URL base del servicio web del SICE
    private val baseUrl = "https://sicenet.itsur.edu.mx/ws/wsalumnos.asmx"
    suspend fun acceso(requestBody: String): HttpResponse {
        return client.post(baseUrl) {
            header("SOAPAction", "\"http://tempuri.org/accesoLogin\"")
            contentType(ContentType.Text.Xml.withParameter("charset", "utf-8"))
            setBody(requestBody)
        }
    }

    /*Recupera los datos del perfil del alumno en formato XML.*/
    suspend fun getPerfil(requestBody: String): HttpResponse {
        return client.post(baseUrl) {
            // 🔑 Agregamos las comillas internas obligatorias para .NET
            header("SOAPAction", "\"http://tempuri.org/getAlumnoAcademicoWithLineamiento\"")
            contentType(ContentType.Text.Xml.withParameter("charset", "utf-8"))
            setBody(requestBody)
        }
    }

    /*Recupera la carga académica actual del alumno. */
    suspend fun getCarga(requestBody: String): HttpResponse {
        return client.post(baseUrl) {
            header("SOAPAction", "http://tempuri.org/getCargaAcademicaByAlumno")
            contentType(ContentType.Text.Xml.withParameter("charset", "utf-8"))
            setBody(requestBody)
        }
    }

    /**
     * Recupera el historial del Kardex del alumno.
     */
    suspend fun getKardex(requestBody: String): HttpResponse {
        return client.post(baseUrl) {
            // 🔑 CORREGIDO: Añadidas las comillas explícitas requeridas por .NET
            header("SOAPAction", "\"http://tempuri.org/getAllKardexConPromedioByAlumno\"")
            contentType(ContentType.Text.Xml.withParameter("charset", "utf-8"))
            setBody(requestBody)
        }
    }

    /*Recupera las calificaciones parciales por unidad.*/

    suspend fun getNotasUnidades(requestBody: String): HttpResponse {
        return client.post(baseUrl) {

            header("SOAPAction", "\"http://tempuri.org/getCalifUnidadesByAlumno\"")
            contentType(ContentType.Text.Xml.withParameter("charset", "utf-8"))
            setBody(requestBody)
        }
    }

    /*Recupera las calificaciones finales de las materias.*/
    suspend fun getCalifFinales(requestBody: String): HttpResponse {
        return client.post(baseUrl) {
            header("SOAPAction", "http://tempuri.org/getAllCalifFinalByAlumnos")
            contentType(ContentType.Text.Xml.withParameter("charset", "utf-8"))
            setBody(requestBody)
        }
    }
}