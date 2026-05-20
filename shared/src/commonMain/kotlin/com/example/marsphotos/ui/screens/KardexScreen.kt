package com.example.marsphotos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.marsphotos.model.Kardex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KardexScreen(
    viewModel: KardexViewModel
) {
    val state = viewModel.uiState
    val isSyncing = state.isLoading

    LaunchedEffect(Unit) {
        viewModel.cargarKardex()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Kardex Académico") }) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            state.materias.firstOrNull()?.let {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "Última sincronización: ${it.fechaSincronizacion}",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            if (isSyncing && state.materias.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.materias) { item ->
                        KardexItem(item)
                    }
                }
            }
        }
    }
}

@Composable
fun KardexItem(kardex: Kardex) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = kardex.materia, fontWeight = FontWeight.Bold)
                Text(text = "${kardex.periodo} | ${kardex.acreditacion}", style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = kardex.calificacion.toString(),
                color = if (kardex.calificacion >= 70) Color(0xFF1976D2) else Color.Red,
                fontWeight = FontWeight.Black
            )
        }
    }
}