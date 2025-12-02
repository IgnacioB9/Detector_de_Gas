package com.example.detectordegas.vistas

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.detectordegas.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Composable
fun LoginView(navController: NavController, auth: FirebaseAuth) {

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var passVisible by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFEFF6FF), Color(0xFFF8FAFC))
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(0.85f),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )


                Text(
                    "Monitor de Gas",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Correo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passVisible = !passVisible }) {
                            Icon(
                                imageVector = if (passVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "mostrar"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = { validarCredencial(username, password, auth, context, navController) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Iniciar sesión")
                }

                Spacer(Modifier.height(10.dp))

                Row {
                    Text("¿No tienes cuenta? ")
                    Text(
                        "Regístrate",
                        color = Color(0xFF0F62FE),
                        modifier = Modifier.clickable {
                            navController.navigate("register")
                        }
                    )
                }
                Row {
                    Text( "¿Olvidaste tu contraseña? ",
                        color = Color(0xFF0F62FE),
                        modifier = Modifier.clickable {
                            navController.navigate("restablecer_contrasena")
                        }
                    )
                }
            }
        }
    }
}

private fun validarCredencial(
    username: String,
    password: String,
    auth: FirebaseAuth,
    context: Context,
    navController: NavController
) {

    if (username.isEmpty() || password.isEmpty()) {
        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
        return
    }

    auth.signInWithEmailAndPassword(username, password).addOnCompleteListener { task ->
        if (task.isSuccessful) {

            val user = auth.currentUser

            // Detectar si cambió la contraseña → metadata
            val metadata = user?.metadata
            val lastPasswordChange = metadata?.lastSignInTimestamp

            if (lastPasswordChange != null) {
                println("Último cambio de contraseña detectado: $lastPasswordChange")
            }

            Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

            navController.navigate("index") {
                popUpTo("login") { inclusive = true }
            }

        } else {
            Toast.makeText(context, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
        }
    }
}


