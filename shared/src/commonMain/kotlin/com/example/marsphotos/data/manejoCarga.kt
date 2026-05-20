package com.example.marsphotos.data

import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Root
import retrofit2.Response

@Root(name = "Envelope", strict = false)
@Namespace(reference = "http://schemas.xmlsoap.org/soap/envelope/", prefix = "soap")
data class CargaSoapResponse(
    @field:Element(name = "Body")
    var body: CargaSoapBody? = null
)

@Root(name = "Body", strict = false)
data class CargaSoapBody(
    @field:Element(name = "getCargaAcademicaByAlumnoResponse", required = false)
    var getCargaResponse: CargaResultWrapper? = null
)

@Root(name = "getCargaAcademicaByAlumnoResponse", strict = false)
data class CargaResultWrapper(
    @field:Element(name = "getCargaAcademicaByAlumnoResult", required = false)
    var result: String? = null
)