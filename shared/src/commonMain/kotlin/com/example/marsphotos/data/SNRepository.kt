package com.example.marsphotos.data

import com.example.marsphotos.model.*
import com.example.marsphotos.network.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

interface SNRepository {
    // Auth & Perfil
    suspend fun acceso(m: String, p: String): String
    suspend fun profile(m: String): ProfileStudent

    // Carga Académica
    suspend fun traerCargaAcademica(): List<CargaAcademica>
    fun obtenerCarga(): Flow<List<CargaAcademica>>
    suspend fun insertLocalCarga(materias: List<CargaAcademica>)

    // Kardex
    suspend fun fetchKardexRemote(): List<Kardex>
    fun obtenerKardexLocal(): Flow<List<Kardex>>
    suspend fun insertarKardexLocal(lista: List<Kardex>)

    // Notas por Unidad
    suspend fun fetchNotasUnidadesRemote(): List<MateriaUnidades>
    fun obtenerNotasLocal(): Flow<List<MateriaUnidades>>
    suspend fun insertarNotasLocal(lista: List<MateriaUnidades>)

    // Calificaciones Finales
    suspend fun fetchCalifFinalesRemote(): List<CalifFinal>
    fun obtenerFinalesLocal(): Flow<List<CalifFinal>>
    suspend fun insertarFinalesLocal(lista: List<CalifFinal>)
}

class NetworkSNRepository(
    private val snApiService: SICENETWService
) : SNRepository {

    // AUTENTICACIÓN
    override suspend fun acceso(m: String, p: String): String {
        return try {
            // Mandamos los parámetros de autenticación directamente usando el servicio Ktor
            val responseString = snApiService.login(m, p)
            if (responseString.contains("\"acceso\":true", ignoreCase = true)) "success" else "invalid"
        } catch (e: Exception) {
            "error"
        }
    }

    // PERFIL
    override suspend fun profile(m: String): ProfileStudent {
        return try {
            val xml = snApiService.getPerfil()
            val jsonContent = Regex("""<getAlumnoAcademicoWithLineamientoResult>([^<]+)""").find(xml)?.groupValues?.get(1)

            if (jsonContent != null) {
                ProfileStudent(
                    matricula = m,
                    nombre = Regex("""\"nombre\":\"([^\"]+)""").find(jsonContent)?.groupValues?.get(1) ?: "Estudiante",
                    carrera = Regex("""\"carrera\":\"([^\"]+)""").find(jsonContent)?.groupValues?.get(1) ?: "Carrera",
                    promedio = Regex("""\"especialidad\":\"([^\"]+)""").find(jsonContent)?.groupValues?.get(1) ?: "Sin Especialidad",
                    semestre = Regex("""\"semActual\":(\d+)""").find(jsonContent)?.groupValues?.get(1) ?: "0",
                    creditos = Regex("""\"cdtosAcumulados\":(\d+)""").find(jsonContent)?.groupValues?.get(1) ?: "0",
                    fechaReins = Regex("""\"fechaReins\":\"([^\"]+)""").find(jsonContent)?.groupValues?.get(1) ?: "No disponible"
                )
            } else {
                ProfileStudent(m, "Error", "Formato inválido", "", "", "", "")
            }
        } catch (e: Exception) {
            ProfileStudent(m, "Error de red", "", "", "", "", "")
        }
    }

    // Carga académica
    override suspend fun traerCargaAcademica(): List<CargaAcademica> {
        return try {
            val response = snApiService.getCargaAcademica()
            // TODO: Ajustar el parseo cuando definamos si usas kotlinx.serialization para los objetos
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Mantenemos los flujos locales vacíos de momento para no romper las firmas de la interfaz
    override fun obtenerCarga(): Flow<List<CargaAcademica>> = flowOf(emptyList())
    override suspend fun insertLocalCarga(materias: List<CargaAcademica>) {}

    // KARDEX
    override suspend fun fetchKardexRemote(): List<Kardex> {
        return try {
            val xmlCompleto = snApiService.getCargaAcademica() // O tu llamada correspondiente a Kardex si la mapeas en el servicio
            val regex = Regex("""<getAllKardexConPromedioByAlumnoResult>([\s\S]*?)</getAllKardexConPromedioByAlumnoResult>""", RegexOption.IGNORE_CASE)
            val contenidoJson = regex.find(xmlCompleto)?.groupValues?.get(1) ?: ""
            if (contenidoJson.isNotEmpty()) parsearKardex(contenidoJson) else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun obtenerKardexLocal(): Flow<List<Kardex>> = flowOf(emptyList())
    override suspend fun insertarKardexLocal(lista: List<Kardex>) {}

    // NOTAS POR UNIDAD
    override suspend fun fetchNotasUnidadesRemote(): List<MateriaUnidades> {
        return try {
            // Reemplazar temporalmente con la simulación hasta estructurar los parseadores multiplataforma
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun obtenerNotasLocal(): Flow<List<MateriaUnidades>> = flowOf(emptyList())
    override suspend fun insertarNotasLocal(lista: List<MateriaUnidades>) {}

    // CALIFICACIONES FINALES
    override suspend fun fetchCalifFinalesRemote(): List<CalifFinal> = emptyList()
    override fun obtenerFinalesLocal(): Flow<List<CalifFinal>> = flowOf(emptyList())
    override suspend fun insertarFinalesLocal(lista: List<CalifFinal>) {}

    // PARSEADORES TEMPORALES
    private fun parsearKardex(jsonString: String): List<Kardex> {
        // Estructuraremos esto usando kotlinx.serialization en el siguiente paso
        return emptyList()
    }
}