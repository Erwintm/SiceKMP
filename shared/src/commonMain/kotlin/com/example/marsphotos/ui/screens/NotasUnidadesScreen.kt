package com.example.marsphotos.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.marsphotos.model.MateriaUnidades

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotasUnidadesScreen(
    viewModel: NotasUnidadesViewModel,
    onVolver: () -> Unit
) {
    val uiState = viewModel.uiState
    val isSyncing = uiState.isLoading

    LaunchedEffect(Unit) {
        viewModel.cargarNotas()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calificaciones por Unidad", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp, end = 4.dp)
                            .clickable { onVolver() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "◁",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            uiState.materias.firstOrNull()?.let {
                if (it.fechaSincronizacion.isNotBlank()) {
                    Text(
                        text = "Última sincronización: ${it.fechaSincronizacion}",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = Color.Gray
                    )
                }
            }

            if (isSyncing && uiState.materias.isEmpty()) {

                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (!isSyncing && uiState.materias.isEmpty()) {

                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No hay calificaciones disponibles localmente.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            } else {
                // Lista de materias
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    items(uiState.materias) { materia ->
                        MateriaNotaCard(materia.materia, materia.unidades)
                    }
                }
            }
        }
    }
}

@Composable
fun MateriaNotaCard(nombre: String, unidades: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            val listaNotas = unidades.split(",").filter { it.isNotBlank() }

            if (listaNotas.isEmpty()) {
                Text("Sin calificaciones aún", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            } else {
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    listaNotas.forEachIndexed { index, nota ->
                        SuggestionChip(
                            onClick = { },
                            label = { Text("U${index + 1}: $nota") },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }
            }
        }
    }
}