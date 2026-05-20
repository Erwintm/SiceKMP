package com.example.marsphotos.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.work.WorkInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotasUnidadesScreen(viewModel: NotasUnidadesViewModel) {
    val context = LocalContext.current
    val uiState = viewModel.uiState
    val workInfos by viewModel.syncWorkInfo.observeAsState()
    val isSyncing = workInfos?.any { it.state == WorkInfo.State.RUNNING } == true

    LaunchedEffect(Unit) {
        val isOnline = checkInternet(context)
        viewModel.cargarNotas(isOnline)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Calificaciones por Unidad") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            uiState.materias.firstOrNull()?.let {
                Text(
                    text = "Última sincronización: ${it.fechaSincronizacion}",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.Gray
                )
            }

            if (isSyncing && uiState.materias.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
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

private fun checkInternet(context: android.content.Context): Boolean {
    val cm = context.getSystemService(ConnectivityManager::class.java)
    val nw = cm.activeNetwork ?: return false
    val actNw = cm.getNetworkCapabilities(nw) ?: return false
    return actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
}