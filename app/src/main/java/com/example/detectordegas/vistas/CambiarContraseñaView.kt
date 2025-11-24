package com.example.detectordegas.vistas

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CambiarContrasenaView(
    navController: NavController,
    auth: FirebaseAuth
) {

    var nuevaPass by remember { mutableStateOf("") }
    var confirmarPass by remember { mutableStateOf("") }

    var passVisible by remember { mutableStateOf(false) }
    var confirmPassVisible by remember { mutableStateOf(false) }

    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cambiar contraseña") },
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

            OutlinedTextField(
                value = nuevaPass,
                onValueChange = { nuevaPass = it },
                label = { Text("Nueva contraseña") },
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passVisible = !passVisible }) {
                        Icon(
                            imageVector = if (passVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "mostrar"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmarPass,
                onValueChange = { confirmarPass = it },
                label = { Text("Confirmar contraseña") },
                visualTransformation = if (confirmPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPassVisible = !confirmPassVisible }) {
                        Icon(
                            imageVector = if (confirmPassVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "mostrar"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(25.dp))

            Button(
                onClick = {

                    if (nuevaPass.isEmpty() || confirmarPass.isEmpty()) {
                        Toast.makeText(navController.context, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (nuevaPass != confirmarPass) {
                        Toast.makeText(navController.context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    mostrarDialogo = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cambiar contraseña")
            }
        }
    }

    // Confirmación del cambio
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Confirmar cambio") },
            text = { Text("¿Seguro que deseas cambiar la contraseña?") },

            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogo = false

                    auth.currentUser?.updatePassword(nuevaPass)?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(navController.context, "Contraseña cambiada", Toast.LENGTH_LONG).show()

                            auth.signOut()

                            navController.navigate("login") {
                                popUpTo("index") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(navController.context, "Error al cambiar la contraseña", Toast.LENGTH_SHORT).show()
                        }
                    }

                }) { Text("Sí, cambiar") }
            },

            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

