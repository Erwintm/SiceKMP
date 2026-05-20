package com.example.marsphotos.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState // IMPORTANTE
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.WorkInfo
import com.example.marsphotos.MarsPhotosApplication
import com.example.marsphotos.model.Kardex
import com.example.marsphotos.model.KardexViewModelFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KardexScreen(
    navController: androidx.navigation.NavController
) {
    val context = LocalContext.current
    val application = context.applicationContext as MarsPhotosApplication
    val isOnline = remember { isNetworkAvailable(context) }

    val viewModel: KardexViewModel = viewModel(
        factory = KardexViewModelFactory(application, application.container.snRepository)
    )

    val state = viewModel.uiState
    val workInfos by viewModel.syncWorkInfo.observeAsState()
    val isSyncing = workInfos?.any { it.state == WorkInfo.State.RUNNING } == true

    LaunchedEffect(Unit) {
        viewModel.cargarKardex(isOnline)
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

            if ((state.isLoading || isSyncing) && state.materias.isEmpty()) {
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

// Función necesaria para el check de internet
@SuppressLint("ServiceCast")
private fun isNetworkAvailable(context: android.content.Context): Boolean {
    val connectivityManager = context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
    return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
}