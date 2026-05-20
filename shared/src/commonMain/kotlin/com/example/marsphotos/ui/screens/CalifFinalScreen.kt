package com.example.marsphotos.ui.screens

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.work.WorkInfo
import com.example.marsphotos.model.CalifFinal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalifFinalScreen(
    navController: NavController,
    viewModel: CalifFinalViewModel
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState

    // Monitoreo del Worker
    val workInfos by viewModel.syncWorkInfo.observeAsState()
    val isSyncing = workInfos?.any { it.state == WorkInfo.State.RUNNING } == true

    LaunchedEffect(Unit) {
        val cm = context.getSystemService(ConnectivityManager::class.java)
        val isOnline = cm.activeNetwork?.let { cm.getNetworkCapabilities(it) }
            ?.let { it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } ?: false

        viewModel.cargarFinales(isOnline)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calificaciones Finales", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // Etiqueta de sincronización
            uiState.listaFinal.firstOrNull()?.let {
                Text(
                    text = "Actualizado: ${it.fechaSincronizacion}",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray
                )
            }

            if (isSyncing && uiState.listaFinal.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(uiState.listaFinal) { item ->
                        CalifFinalCard(item)
                    }
                }
            }
        }
    }
}
// ... (Tu CalifFinalCard se queda exactamente igual)
// ... (Todo el código que ya tienes de CalifFinalScreen)

@Composable
fun CalifFinalCard(final: CalifFinal) {
    val colorCalif = if (final.calificacion >= 70) Color(0xFF388E3C) else Color.Red

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = final.materia,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Grupo: ${final.grupo} • ${final.acreditacion}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Surface(
                color = colorCalif.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (final.calificacion > 0) final.calificacion.toString() else "NA",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = colorCalif,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}