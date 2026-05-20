package com.example.marsphotos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
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
import com.example.marsphotos.model.CargaAcademica

@Composable
fun CargaAcademicaScreen(
    viewModel: CargaViewModel
) {
    val listaCarga by viewModel.materias.collectAsState()
    val estaSincronizando = viewModel.estaSincronizando

    LaunchedEffect(Unit) {
        viewModel.sincronizarCarga()
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2))) {
        if (listaCarga.isNotEmpty()) {
            val fecha = listaCarga.first().fechaSincronizacion
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFE8F5E9)
            ) {
                Text(
                    text = "Actualizado: $fecha",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF2E7D32),
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