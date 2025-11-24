package com.example.detectordegas.vistas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilView(navController: NavController, auth: FirebaseAuth) {

    Scaffold(
        topBar = { TopAppBar(title = { Text("Perfil") }) },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->

        Column(
            modifier = Modifier.padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("Configuración de cuenta", fontSize = 24.sp)

            Button(
                onClick = { navController.navigate("cambiar_contrasena") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cambiar contraseña")
            }

            Button(
                onClick = {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo("index") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión", color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}
