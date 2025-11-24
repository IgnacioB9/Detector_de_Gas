package com.example.detectordegas.vistas

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun HistorialView(navController: NavController) {

    val datosFake = List(20) { i -> "Lectura #$i - ${(100..900).random()} ppm" }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Historial de Gases") }) },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->

        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            items(datosFake) { lectura ->
                Card(
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        lectura,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
