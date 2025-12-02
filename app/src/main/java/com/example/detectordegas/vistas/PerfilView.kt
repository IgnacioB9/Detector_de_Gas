package com.example.detectordegas.vistas

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilView(navController: NavController, auth: FirebaseAuth) {
    // Obtener el email
    val userEmail = auth.currentUser?.email ?: ""
    // 4 caracteres como nombre de usuario
    val userName = if (userEmail.length >= 4) userEmail.substring(0, 4) else userEmail

    Scaffold(
        topBar = { TopAppBar(title = { Text("Perfil") }) },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->

        Column(
            modifier = Modifier.padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Icono de usuario
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Icono de usuario",
                    modifier = Modifier.size(64.dp),  // Tamaño del icono
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text("Configuración de cuenta", fontSize = 24.sp, textAlign = TextAlign.Center)
            }

            // Nombre usuario
            Text(
                text = "Usuario: $userName",
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

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

