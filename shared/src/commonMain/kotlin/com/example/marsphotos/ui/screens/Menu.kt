package com.example.marsphotos.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.marsphotos.model.MenuOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(navController: NavController) {

    val options = listOf(
        MenuOption("Carga\nAcadémica", "carga", Color(0xFF1976D2)),
        MenuOption("Kardex\nEscolar", "kardex", Color(0xFF388E3C)),
        MenuOption("Calificaciones\npor Unidad", "notas", Color(0xFFFBC02D)),
        MenuOption("Calificaciones\nFinales", "finales", Color(0xFFE64A19))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Portal Integral", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(options) { option ->
                MenuCard(option) {
                    navController.navigate(option.route)
                }
            }
        }
    }
}

@Composable
fun MenuCard(option: MenuOption, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = option.color.copy(alpha = 0.1f)
        ),
        border = BorderStroke(2.dp, option.color.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Text(
                text = option.title,
                style = MaterialTheme.typography.titleLarge,
                color = option.color,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 28.sp
            )
        }
    }
}