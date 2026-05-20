package com.example.marsphotos.model

data class KardexResponse(
    val lstKardex: List<KardexRaw>
)

data class KardexRaw(
    val ClvMat: String?,
    val Materia: String?,
    val Calif: Int?,
    val Acred: String?,
    val P1: String?,
    val A1: String?
)