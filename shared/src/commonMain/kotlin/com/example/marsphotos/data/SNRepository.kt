package com.example.marsphotos.data

import com.example.marsphotos.model.*
import com.example.marsphotos.network.*
import kotlinx.coroutines.flow.Flow
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map

interface SNRepository {
    // Auth & Perfil
    suspend fun acceso(m: String, p: String): String
    suspend fun profile(m: String): ProfileStudent
    fun obtenerPerfilLocal(m: String): Flow<ProfileStudent?>
    suspend fun insertarPerfilLocal(p: ProfileStudent)

    // Carga Académica
    suspend fun traerCargaAcademica(): List<CargaAcademica>
    fun obtenerCarga(): Flow<List<CargaAcademica>>
    suspend fun insertLocalCarga(materias: List<CargaAcademica>)

    // Kardex
    suspend fun fetchKardexRemote(): List<com.example.marsphotos.model.Kardex>
    fun obtenerKardexLocal(): Flow<List<com.example.marsphotos.model.Kardex>>
    suspend fun insertarKardexLocal(lista: List<com.example.marsphotos.model.Kardex>)

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
    private val snApiService: SICENETWService,
    private val database: SNDatabase
) : SNRepository {

    private val queries = database.sNDatabaseQueries

    // ==========================================
    // 🔐 AUTENTICACIÓN
    // ==========================================
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

    // ==========================================
    // 👤 PERFIL
    // ==========================================
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

                val estudiante = ProfileStudent(
                    matricula = m,
                    nombre = nombre,
                    carrera = carrera,
                    promedio = especialidad,
                    semestre = semestre,
                    creditos = creditos,
                    fechaReins = fechaReins,
                    fechaSincronizacion = "2026-05"
                )

                insertarPerfilLocal(estudiante)
                estudiante
            } else {
                ProfileStudent(m, "Error", "Formato XML inválido", "", "", "", "", "")
            }
        } catch (e: Exception) {
            println("SICE_PROFILE_ERROR: ${e.message}")
            ProfileStudent(m, "Error de red", "${e.message}", "", "", "", "", "")
        }
    }

    override fun obtenerPerfilLocal(m: String): Flow<ProfileStudent?> {
        return queries.obtenerPerfil(m)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { db ->
                if (db == null) null else ProfileStudent(
                    matricula = db.matricula,
                    nombre = db.nombre,
                    carrera = db.carrera,
                    promedio = db.promedio,
                    semestre = db.semestre,
                    creditos = db.creditos,
                    fechaReins = db.fechaReins,
                    fechaSincronizacion = db.fechaSincronizacion
                )
            }
    }

    override suspend fun insertarPerfilLocal(p: ProfileStudent) {
        queries.insertarPerfil(
            p.matricula,
            p.nombre,
            p.carrera,
            p.promedio,
            p.semestre,
            p.creditos,
            p.fechaReins,
            p.fechaSincronizacion.ifBlank { "2026-05" }
        )
    }

    // ==========================================
    // 📅 CARGA ACADÉMICA
    // ==========================================
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
        return queries.obtenerCarga()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { listaDb ->
                listaDb.map { db ->
                    CargaAcademica(
                        id = db.id,
                        Semipresencial = db.semipresencial,
                        Observaciones = db.observaciones,
                        Docente = db.docente,
                        clvOficial = db.clvOficial,
                        Sabado = db.sabado,
                        Viernes = db.viernes,
                        Jueves = db.jueves,
                        Miercoles = db.miercoles,
                        Martes = db.martes,
                        Lunes = db.lunes,
                        EstadoMateria = db.estadoMateria,
                        CreditosMateria = db.creditosMateria.toInt(),
                        Materia = db.materia,
                        Grupo = db.grupo,
                        fechaSincronizacion = db.fechaSincronizacion
                    )
                }
            }
    }

    override suspend fun insertLocalCarga(materias: List<CargaAcademica>) {
        queries.transaction {
            queries.borrarCarga()
            materias.forEach { m ->
                queries.insertarCarga(
                    m.id,
                    m.Semipresencial,
                    m.Observaciones,
                    m.Docente,
                    m.clvOficial,
                    m.Sabado,
                    m.Viernes,
                    m.Jueves,
                    m.Miercoles,
                    m.Martes,
                    m.Lunes,
                    m.EstadoMateria,
                    m.CreditosMateria.toLong(),
                    m.Materia,
                    m.Grupo,
                    m.fechaSincronizacion.ifBlank { "2026-05" }
                )
            }
        }
    }

    // ==========================================
    // 📜 KARDEX
    // ==========================================
    override suspend fun fetchKardexRemote(): List<com.example.marsphotos.model.Kardex> {
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

    override fun obtenerKardexLocal(): Flow<List<com.example.marsphotos.model.Kardex>> {
        return queries.obtenerKardex()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { listaDb ->
                listaDb.map { db ->
                    com.example.marsphotos.model.Kardex(
                        id = db.id.toInt(),
                        clvMateria = db.clvMateria,
                        materia = db.materia,
                        calificacion = db.calificacion.toInt(),
                        acreditacion = db.acreditacion,
                        periodo = db.periodo,
                        fechaSincronizacion = db.fechaSincronizacion
                    )
                }
            }
    }

    // 🚨 CORRECCIÓN: Se restauró el 'override' con el tipo de paquete correcto
    override suspend fun insertarKardexLocal(lista: List<com.example.marsphotos.model.Kardex>) {
        queries.transaction {
            queries.borrarKardex()
            lista.forEach { k ->
                queries.insertarKardex(
                    k.clvMateria,
                    k.materia,
                    k.calificacion.toLong(),
                    k.acreditacion,
                    k.periodo,
                    k.fechaSincronizacion.ifBlank { "2026-05" }
                )
            }
        }
    }

    // ==========================================
    // 📝 NOTAS POR UNIDAD
    // ==========================================
    override suspend fun fetchNotasUnidadesRemote(): List<MateriaUnidades> {
        return try {
            println("📡 [Notas] Enviando petición al SICE...")
            val xmlEnvelope = getNotasUnidadesXml()
            val response = snApiService.getNotasUnidades(xmlEnvelope)
            val xmlCompleto = response.bodyAsText()

            val contenidoJson = extraerJsonDeXml(xmlCompleto)

            if (contenidoJson.isNotEmpty() && contenidoJson != "null") {
                val listaNotas = parsearNotas(contenidoJson)
                if (listaNotas.isNotEmpty()) {
                    insertarNotasLocal(listaNotas)
                }
                listaNotas
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
        return queries.obtenerNotas()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { listaDb ->
                listaDb.map { db ->
                    MateriaUnidades(
                        id = db.id.toInt(),
                        materia = db.materia,
                        unidades = db.unidades,
                        fechaSincronizacion = db.fechaSincronizacion
                    )
                }
            }
    }

    override suspend fun insertarNotasLocal(lista: List<MateriaUnidades>) {
        queries.transaction {
            queries.borrarNotas()
            lista.forEach { n ->
                queries.insertarNotas(
                    n.materia,
                    n.unidades,
                    n.fechaSincronizacion.ifBlank { "2026-05" }
                )
            }
        }
    }

    // ==========================================
    // 🏁 CALIFICACIONES FINALES
    // ==========================================
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
                        id = 0,
                        materia = raw.materia ?: "",
                        grupo = raw.grupo ?: "",
                        calificacion = raw.calif ?: 0,
                        accreditation = raw.acred ?: "",
                        fechaSincronizacion = "2026-05"
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

    override fun obtenerFinalesLocal(): Flow<List<CalifFinal>> {
        return queries.obtenerFinales()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { listaDb ->
                listaDb.map { db ->
                    CalifFinal(
                        id = db.id.toInt(),
                        materia = db.materia,
                        grupo = db.grupo,
                        calificacion = db.calificacion.toInt(),
                        accreditation = db.acreditacion,
                        fechaSincronizacion = db.fechaSincronizacion
                    )
                }
            }
    }

    override suspend fun insertarFinalesLocal(lista: List<CalifFinal>) {
        queries.transaction {
            queries.borrarFinales()
            lista.forEach { f ->
                queries.insertarFinales(
                    f.materia,
                    f.grupo,
                    f.calificacion.toLong(),
                    f.accreditation,
                    f.fechaSincronizacion.ifBlank { "2026-05" }
                )
            }
        }
    }

    // ==========================================
    // 🛠️ AUXILIARES Y PARSEADORES
    // ==========================================
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

    private fun parsearKardex(jsonString: String): List<com.example.marsphotos.model.Kardex> {
        return try {
            val jsonLimpio = jsonString.replace("\\", "")
            val jsonConfig = Json { ignoreUnknownKeys = true }

            val respuestaRaw = jsonConfig.decodeFromString<KardexResponse>(jsonLimpio)

            respuestaRaw.lstKardex.map { raw ->
                Kardex(
                    id = 0,
                    clvMateria = raw.ClvMat ?: "",
                    materia = raw.Materia ?: "",
                    calificacion = raw.Calif ?: 0,
                    acreditacion = raw.Acred ?: "",
                    periodo = "${raw.P1 ?: ""} ${raw.A1 ?: ""}".trim(),
                    fechaSincronizacion = "2026-05"
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
                    id = 0,
                    materia = raw.Materia ?: "Materia sin nombre",
                    unidades = notes,
                    fechaSincronizacion = "2026-05"
                )
            }
            resultadoMapeado
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // ==========================================
    // 🛠️ GENERADORES XML (SOAP)
    // ==========================================
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