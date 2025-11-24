package com.example.detectordegas.vistas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun NotificacionesView(navController: NavController) {

    val notificaciones = listOf(
        "Alerta: Nivel crítico 800 ppm",
        "Alerta: Nivel moderado 550 ppm",
        "Sensor reconectado",
        "Nivel seguro restaurado"
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Notificaciones") }) },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->

        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            items(notificaciones) { msg ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                        Spacer(Modifier.width(12.dp))
                        Text(msg, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}
