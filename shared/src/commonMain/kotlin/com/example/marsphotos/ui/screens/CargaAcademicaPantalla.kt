package com.example.marsphotos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.marsphotos.model.CargaAcademica
import com.example.marsphotos.model.CargaViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.work.WorkInfo

@Composable
fun CargaAcademicaScreen(
    viewModel: CargaViewModel,
    navController: NavController,
    isOnline: Boolean
) {
    val listaCarga by viewModel.materias.collectAsState()
    val workInfos by viewModel.syncWorkInfo.observeAsState()


    val estaSincronizando = workInfos?.any {
        it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED
    } == true


    LaunchedEffect(isOnline) {
        if (isOnline) viewModel.sincronizarCarga()
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2))) {


        if (listaCarga.isNotEmpty()) {
            val fecha = listaCarga.first().fechaSincronizacion
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (isOnline) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
            ) {
                Text(
                    text = if (isOnline) "Actualizado: $fecha" else "Modo Offline - Datos del $fecha",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isOnline) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                    textAlign = TextAlign.Center
                )
            }
        }


        Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFF1B5E20)) {
            Row(modifier = Modifier.padding(12.dp)) {
                Text("MATERIA / DOCENTE", color = Color.White, modifier = Modifier.weight(2.5f), style = MaterialTheme.typography.labelLarge)
                Text("HORARIO", color = Color.White, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End, style = MaterialTheme.typography.labelLarge)
            }
        }

        if (estaSincronizando) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color(0xFFE65100))
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(listaCarga) { materia ->
                CargaItemRow(materia)
            }
        }
    }
}

@Composable
fun CargaItemRow(carga: CargaAcademica) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp).height(IntrinsicSize.Min)) {
            Column(modifier = Modifier.weight(2.5f)) {
                Text(carga.Materia, fontWeight = FontWeight.Bold, fontSize = 13.sp, lineHeight = 16.sp)
                Text(carga.Docente, color = Color(0xFFE65100), fontSize = 11.sp, lineHeight = 14.sp)
            }

            Column(modifier = Modifier.weight(1.5f), horizontalAlignment = Alignment.End) {
                val dias = listOf("L" to carga.Lunes, "M" to carga.Martes, "Mi" to carga.Miercoles, "J" to carga.Jueves, "V" to carga.Viernes)
                dias.forEach { (label, horario) ->
                    if (horario.isNotBlank()) {
                        Text("$label: ${horario.replace(" Aula:", " | ")}", fontSize = 10.sp, maxLines = 1)
                    }
                }
            }
        }
    }
}