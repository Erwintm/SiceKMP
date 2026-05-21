package com.example.marsphotos.data

import com.example.marsphotos.database.CalifFinalDao
import com.example.marsphotos.database.CalifFinalEntity
import com.example.marsphotos.model.*
import com.example.marsphotos.network.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import io.ktor.client.statement.*

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
    private val snApiService: SICENETWService,
    private val califFinalDao: CalifFinalDao
) : SNRepository {

    // 🔐 AUTENTICACIÓN
    override suspend fun acceso(m: String, p: String): String {
        return try {
            val xmlEnvelope = getLoginXml(m, p)
            val response = snApiService.acceso(xmlEnvelope)
            val responseString = response.bodyAsText()

            println("SICE_RESPONSE_RAW: $responseString")

            // Flexibilizamos la búsqueda limpiando comillas y espacios de la respuesta SOAP
            val respuestaLimpia = responseString.replace("\"", "").replace(" ", "")

            if (respuestaLimpia.contains("acceso:true", ignoreCase = true)) {
                "success"
            } else {
                "invalid"
            }
        } catch (e: Exception) {
            println("SICE_RESPONSE_ERROR: ${e.message}")
            "error"
        }
    }

    // 👤 PERFIL (Optimizado para procesar la respuesta real del SICE)
    override suspend fun profile(m: String): ProfileStudent {
        return try {
            val xmlEnvelope = getPerfilXml(m)
            val response = snApiService.getPerfil(xmlEnvelope)
            val xml = response.bodyAsText()

            println("SICE_PROFILE_RAW: $xml")

            // Extraemos todo lo que está dentro de la etiqueta del resultado
            val jsonContent = Regex("""<getAlumnoAcademicoWithLineamientoResult>([\s\S]*?)</getAlumnoAcademicoWithLineamientoResult>""")
                .find(xml)?.groupValues?.get(1)

            if (!jsonContent.isNullOrEmpty()) {
                // Removemos los escapes de barras invertidas para trabajar con texto plano limpio
                val jsonLimpio = jsonContent.replace("\\", "")

                // Buscamos los valores usando expresiones regulares tolerantes
                val nombre = Regex(""""nombre":"([^"]+)"""").find(jsonLimpio)?.groupValues?.get(1) ?: "Estudiante"
                val carrera = Regex(""""carrera":"([^"]+)"""").find(jsonLimpio)?.groupValues?.get(1) ?: "Carrera"

                // El SICE en especialidad a veces devuelve la cadena de la especialidad directamente
                val especialidad = Regex(""""especialidad":"([^"]+)"""").find(jsonLimpio)?.groupValues?.get(1) ?: "Sin Especialidad"

                // Los números pueden o no venir acompañados de comillas según la versión del SICE
                val semestre = Regex(""""semActual":\s*"?(\d+)"?""").find(jsonLimpio)?.groupValues?.get(1) ?: "0"
                val creditos = Regex(""""cdtosAcumulados":\s*"?(\d+)"?""").find(jsonLimpio)?.groupValues?.get(1) ?: "0"
                val fechaReins = Regex(""""fechaReins":"([^"]+)"""").find(jsonLimpio)?.groupValues?.get(1) ?: "No disponible"

                ProfileStudent(
                    matricula = m,
                    nombre = nombre,
                    carrera = carrera,
                    promedio = especialidad, // Lo mapeamos a especialidad para que coincida con tu interfaz
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
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun obtenerCarga(): Flow<List<CargaAcademica>> = flowOf(emptyList())
    override suspend fun insertLocalCarga(materias: List<CargaAcademica>) {}

    // 📜 KARDEX
    override suspend fun fetchKardexRemote(): List<Kardex> {
        return try {
            val xmlEnvelope = getKardexXml()
            val response = snApiService.getKardex(xmlEnvelope)
            val xmlCompleto = response.bodyAsText()
            val regex = Regex("""<getAllKardexConPromedioByAlumnoResult>([\s\S]*?)</getAllKardexConPromedioByAlumnoResult>""", RegexOption.IGNORE_CASE)
            val contenidoJson = regex.find(xmlCompleto)?.groupValues?.get(1) ?: ""
            if (contenidoJson.isNotEmpty()) parsearKardex(contenidoJson) else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun obtenerKardexLocal(): Flow<List<Kardex>> = flowOf(emptyList())
    override suspend fun insertarKardexLocal(lista: List<Kardex>) {}

    // 📝 NOTAS POR UNIDAD
    override suspend fun fetchNotasUnidadesRemote(): List<MateriaUnidades> {
        return try {
            val xmlEnvelope = getNotasUnidadesXml()
            val response = snApiService.getNotesUnidades(xmlEnvelope)
            val xmlCompleto = response.bodyAsText()
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun obtenerNotasLocal(): Flow<List<MateriaUnidades>> = flowOf(emptyList())
    override suspend fun insertarNotasLocal(lista: List<MateriaUnidades>) {}

    // 🏁 CALIFICACIONES FINALES
    override suspend fun fetchCalifFinalesRemote(): List<CalifFinal> {
        return try {
            val xmlEnvelope = getCalifFinalXml()
            val response = snApiService.getCalifFinales(xmlEnvelope)
            val xmlCompleto = response.bodyAsText()
            val listaRemota = emptyList<CalifFinal>()
            if (listaRemota.isNotEmpty()) {
                insertarFinalesLocal(listaRemota)
            }
            listaRemota
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun obtenerFinalesLocal(): Flow<List<CalifFinal>> {
        return califFinalDao.obtenerTodasLasCalificacionesAsFlow().map { entidades ->
            entidades.map { entidad ->
                CalifFinal(
                    materia = entidad.materia,
                    grupo = entidad.grupo,
                    calificacion = entidad.calificacion,
                    accreditation = entidad.accreditation
                )
            }
        }
    }

    override suspend fun insertarFinalesLocal(lista: List<CalifFinal>) {
        califFinalDao.borrarTodas()
        for (item in lista) {
            califFinalDao.guardarCalificacion(
                CalifFinalEntity(
                    materia = item.materia,
                    grupo = item.grupo,
                    calificacion = item.calificacion,
                    accreditation = item.accreditation
                )
            )
        }
    }

    // 🛠️ XML Generators (Únicos y Actualizados)
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

    private fun getPerfilXml(matricula: String): String = """<?xml version="1.0" encoding="utf-8"?><soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"><soap:Body><getAlumnoAcademicoWithLineamiento xmlns="http://tempuri.org/" /></soap:Body></soap:Envelope>"""
    private fun getCargaXml(): String = """<?xml version="1.0" encoding="utf-8"?><soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"><soap:Body><getCargaAcademicaByAlumno xmlns="http://tempuri.org/" /></soap:Body></soap:Envelope>"""
    private fun getKardexXml(): String = """<?xml version="1.0" encoding="utf-8"?><soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"><soap:Body><getAllKardexConPromedioByAlumno xmlns="http://tempuri.org/" /></soap:Body></soap:Envelope>"""
    private fun getNotasUnidadesXml(): String = """<?xml version="1.0" encoding="utf-8"?><soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"><soap:Body><getCalifUnidadesByAlumno xmlns="http://tempuri.org/" /></soap:Body></soap:Envelope>"""
    private fun getCalifFinalXml(): String = """<?xml version="1.0" encoding="utf-8"?><soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"><soap:Body><getAllCalifFinalByAlumnos xmlns="http://tempuri.org/" /></soap:Body></soap:Envelope>"""

    private fun parsearKardex(jsonString: String): List<Kardex> = emptyList()
}