package com.example.detectordegas.vistas

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestablecerContrasenaView(
    navController: NavController,
    auth: FirebaseAuth
) {
    var email by remember { mutableStateOf("") }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Restablecer contraseña") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ingresa tu correo electrónico para recibir un enlace de restablecimiento.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(25.dp))

            Button(
                onClick = {
                    if (email.isEmpty()) {
                        Toast.makeText(navController.context, "Ingresa un correo electrónico", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(navController.context, "Ingresa un correo válido", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    mostrarDialogo = true
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Enviar enlace de restablecimiento")
                }
            }
        }
    }

    // Diálogo de confirmación
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Confirmar envío") },
            text = { Text("¿Enviar enlace de restablecimiento a $email?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogo = false
                    isLoading = true
                    enviarEnlaceReset(email, navController, auth) { success ->
                        isLoading = false
                        if (success) {
                            navController.navigate("login")  // Navega al login después del envío
                        }
                    }
                }) { Text("Sí, enviar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Función para envío del email de reset
private fun enviarEnlaceReset(
    email: String,
    navController: NavController,
    auth: FirebaseAuth,
    onComplete: (Boolean) -> Unit
) {
    auth.sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(navController.context, "Enlace enviado a $email. Revisa tu bandeja de entrada.", Toast.LENGTH_LONG).show()
                onComplete(true)
            } else {
                val errorMessage = when (task.exception?.message) {
                    "No existe el usuario o" ->
                        "El correo electrónico no está registrado en la base de datos."
                    else -> "Error al enviar el enlace: ${task.exception?.message}"
                }
                Toast.makeText(navController.context, errorMessage, Toast.LENGTH_LONG).show()
                onComplete(false)
            }
        }
}
