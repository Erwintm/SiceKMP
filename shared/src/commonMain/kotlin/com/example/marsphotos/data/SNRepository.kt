package com.example.marsphotos.data

import com.example.marsphotos.model.*
import com.example.marsphotos.network.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json

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

    // 🔐 AUTENTICACIÓN
    override suspend fun acceso(m: String, p: String): String {
        return try {
            val xmlEnvelope = getLoginXml(m, p)
            val response = snApiService.acceso(xmlEnvelope)
            val responseString = response.bodyAsText()

            println("SICE_RESPONSE_RAW: $responseString")

            if (responseString.contains("\"acceso\":true", ignoreCase = true)) {
                "success"
            } else {
                "invalid"
            }
        } catch (e: Exception) {
            println("SICE_RESPONSE_ERROR: ${e.message}")
            "error"
        }
    }

    // 👤 PERFIL
    override suspend fun profile(m: String): ProfileStudent {
        return try {
            val xmlEnvelope = getPerfilXml(m)
            val response = snApiService.getPerfil(xmlEnvelope)
            val xml = response.bodyAsText()

            println("SICE_PROFILE_RAW: $xml")

            val jsonContent = Regex("""<getAlumnoAcademicoWithLineamientoResult>([^<]+)""")
                .find(xml)?.groupValues?.get(1)

            if (jsonContent != null) {
                val jsonLimpio = jsonContent.replace("\\", "")

                val nombre = Regex(""""nombre":"([^"]+)"""").find(jsonLimpio)?.groupValues?.get(1) ?: "Estudiante"
                val carrera = Regex(""""carrera":"([^"]+)"""").find(jsonLimpio)?.groupValues?.get(1) ?: "Carrera"
                val especialidad = Regex(""""especialidad":"([^"]+)"""").find(jsonLimpio)?.groupValues?.get(1) ?: "Sin Especialidad"
                val semestre = Regex(""""semActual":\s*"?(\d+)"?""").find(jsonLimpio)?.groupValues?.get(1) ?: "0"
                val creditos = Regex(""""cdtosAcumulados":\s*"?(\d+)"?""").find(jsonLimpio)?.groupValues?.get(1) ?: "0"
                val fechaReins = Regex(""""fechaReins":"([^"]+)"""").find(jsonLimpio)?.groupValues?.get(1) ?: "No disponible"

                ProfileStudent(
                    matricula = m,
                    nombre = nombre,
                    carrera = carrera,
                    promedio = especialidad,
                    semestre = semestre,
                    creditos = creditos,
                    fechaReins = fechaReins
                )
            } else {
                ProfileStudent(m, "Error", "Formato XML inválido", "", "", "", "")
            }
        } catch (e: Exception) {
            println("SICE_PROFILE_ERROR: ${e.message}")
            ProfileStudent(m, "Error de red", "${e.message}", "", "", "", "")
        }
    }

    // 📅 CARGA ACADÉMICA
    override suspend fun traerCargaAcademica(): List<CargaAcademica> {
        return try {
            val xmlEnvelope = getCargaXml()
            val response = snApiService.getCarga(xmlEnvelope)
            val xmlCompleto = response.bodyAsText()

            val jsonString = extraerJsonDeXml(xmlCompleto)

            if (jsonString.isNotBlank()) {
                val jsonConfig = Json { ignoreUnknownKeys = true }
                val listaMaterias = jsonConfig.decodeFromString<List<CargaAcademica>>(jsonString)

                if (listaMaterias.isNotEmpty()) {
                    insertLocalCarga(listaMaterias)
                }
                listaMaterias
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun obtenerCarga(): Flow<List<CargaAcademica>> {
        return flowOf(emptyList())
    }

    override suspend fun insertLocalCarga(materias: List<CargaAcademica>) {}

    // 📜 KARDEX
    override suspend fun fetchKardexRemote(): List<Kardex> {
        return try {
            val xmlEnvelope = getKardexXml()
            val response = snApiService.getKardex(xmlEnvelope)
            val xmlCompleto = response.bodyAsText()

            val contenidoJson = extraerJsonDeXml(xmlCompleto)

            if (contenidoJson.isNotEmpty() && contenidoJson != "null") {
                val listaKardex = parsearKardex(contenidoJson)
                if (listaKardex.isNotEmpty()) {
                    insertarKardexLocal(listaKardex)
                }
                listaKardex
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun obtenerKardexLocal(): Flow<List<Kardex>> {
        return flowOf(emptyList())
    }

    override suspend fun insertarKardexLocal(lista: List<Kardex>) {}

    // 📝 NOTAS POR UNIDAD
    override suspend fun fetchNotasUnidadesRemote(): List<MateriaUnidades> {
        return try {
            println("📡 [Notas] Enviando petición al SICE...")
            val xmlEnvelope = getNotasUnidadesXml()
            val response = snApiService.getNotasUnidades(xmlEnvelope)
            val xmlCompleto = response.bodyAsText()

            val contenidoJson = extraerJsonDeXml(xmlCompleto)

            if (contenidoJson.isNotEmpty() && contenidoJson != "null") {
                parsearNotas(contenidoJson)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            println("❌ Error crítico en Notas Unidades: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    override fun obtenerNotasLocal(): Flow<List<MateriaUnidades>> {
        return flowOf(emptyList())
    }

    override suspend fun insertarNotasLocal(lista: List<MateriaUnidades>) {}

    // 🏁 CALIFICACIONES FINALES
    override suspend fun fetchCalifFinalesRemote(): List<CalifFinal> {
        return try {
            val xmlEnvelope = getCalifFinalXml()
            val response = snApiService.getCalifFinales(xmlEnvelope)
            val xmlCompleto = response.bodyAsText()

            val contenidoJson = extraerJsonDeXml(xmlCompleto)

            if (contenidoJson.isNotEmpty() && contenidoJson != "null") {
                val jsonLimpio = contenidoJson.replace("\\", "").trim()
                val jsonConfig = Json { ignoreUnknownKeys = true }

                val listaRaw = jsonConfig.decodeFromString<List<FinalRaw>>(jsonLimpio)
                val listaRemota = listaRaw.map { raw ->
                    CalifFinal(
                        materia = raw.materia ?: "",
                        grupo = raw.grupo ?: "",
                        calificacion = raw.calif ?: 0,
                        accreditation = raw.acred ?: ""
                    )
                }

                if (listaRemota.isNotEmpty()) {
                    insertarFinalesLocal(listaRemota)
                }
                listaRemota
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            println("❌ Error en Calif Finales KMP: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    // 🎯 Flujo local vacío en memoria para evitar colgar de un Dao inexistente
    override fun obtenerFinalesLocal(): Flow<List<CalifFinal>> {
        return flowOf(emptyList())
    }

    override suspend fun insertarFinalesLocal(lista: List<CalifFinal>) {}

    // 🛠️ AUXILIARES Y PARSEADORES
    private fun extraerJsonDeXml(xml: String): String {
        return try {
            val inicioArreglo = xml.indexOf("[")
            val inicioObjeto = xml.indexOf("{")

            val inicio = if (inicioArreglo != -1 && (inicioObjeto == -1 || inicioArreglo < inicioObjeto)) inicioArreglo else inicioObjeto
            val fin = if (inicio == inicioArreglo) xml.lastIndexOf("]") + 1 else xml.lastIndexOf("}") + 1

            if (inicio != -1 && fin != -1) {
                xml.substring(inicio, fin)
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    private fun parsearKardex(jsonString: String): List<Kardex> {
        return try {
            val jsonLimpio = jsonString.replace("\\", "")
            val jsonConfig = Json { ignoreUnknownKeys = true }

            val respuestaRaw = jsonConfig.decodeFromString<KardexResponse>(jsonLimpio)

            respuestaRaw.lstKardex.map { raw ->
                Kardex(
                    clvMateria = raw.ClvMat ?: "",
                    materia = raw.Materia ?: "",
                    calificacion = raw.Calif ?: 0,
                    acreditacion = raw.Acred ?: "",
                    periodo = "${raw.P1 ?: ""} ${raw.A1 ?: ""}".trim(),
                    fechaSincronizacion = ""
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun parsearNotas(jsonString: String): List<MateriaUnidades> {
        return try {
            val jsonLimpio = jsonString.trim()
            val jsonConfig = Json {
                ignoreUnknownKeys = true
                isLenient = true
            }

            val listaRaw = jsonConfig.decodeFromString<List<UnidadesRaw>>(jsonLimpio)

            val resultadoMapeado = listaRaw.map { raw ->
                val notes = listOf(raw.C1, raw.C2, raw.C3, raw.C4, raw.C5, raw.C6, raw.C7)
                    .map { nota ->
                        if (nota == null || nota.trim().lowercase() == "null" || nota.isBlank()) {
                            "-"
                        } else {
                            nota.trim()
                        }
                    }
                    .joinToString(",")

                MateriaUnidades(
                    materia = raw.Materia ?: "Materia sin nombre",
                    unidades = notes,
                    fechaSincronizacion = ""
                )
            }
            resultadoMapeado
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // 🛠️ GENERADORES XML (SOAP)
    private fun getLoginXml(usuario: String, contrasenia: String): String = """
        <?xml version="1.0" encoding="utf-8"?>
        <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
          <soap:Body>
            <accesoLogin xmlns="http://tempuri.org/">
              <strMatricula>$usuario</strMatricula>
              <strContrasenia>$contrasenia</strContrasenia>
              <tipoUsuario>ALUMNO</tipoUsuario>
            </accesoLogin>
          </soap:Body>
        </soap:Envelope>
    """.trimIndent()

    private fun getKardexXml(): String = """
        <?xml version="1.0" encoding="utf-8"?>
        <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
          <soap:Body>
            <getAllKardexConPromedioByAlumno xmlns="http://tempuri.org/">
              <aluLineamiento>1</aluLineamiento>
            </getAllKardexConPromedioByAlumno>
          </soap:Body>
        </soap:Envelope>
    """.trimIndent()

    private fun getPerfilXml(matricula: String): String = """<?xml version="1.0" encoding="utf-8"?><soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"><soap:Body><getAlumnoAcademicoWithLineamiento xmlns="http://tempuri.org/" /></soap:Body></soap:Envelope>"""
    private fun getCargaXml(): String = """<?xml version="1.0" encoding="utf-8"?><soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"><soap:Body><getCargaAcademicaByAlumno xmlns="http://tempuri.org/" /></soap:Body></soap:Envelope>"""
    private fun getNotasUnidadesXml(): String = """<?xml version="1.0" encoding="utf-8"?><soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"><soap:Body><getCalificacionesUnidadesByAlumno xmlns="http://tempuri.org/" /></soap:Body></soap:Envelope>"""
    private fun getCalifFinalXml(): String = """<?xml version="1.0" encoding="utf-8"?><soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"><soap:Body><getCalificacionesFinalesByAlumno xmlns="http://tempuri.org/" /></soap:Body></soap:Envelope>"""
}