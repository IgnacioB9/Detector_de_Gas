package com.example.detectordegas.vistas

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun BottomNavBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("index") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("historial") },
            icon = { Icon(Icons.Default.History, contentDescription = "Historial") },
            label = { Text("Historial") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("notificaciones") },
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Alertas") },
            label = { Text("Notificaciones") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("perfil") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") }
        )
    }
}
