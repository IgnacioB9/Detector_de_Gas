package com.example.detectordegas.vistas

import android.content.Context
import android.util.Patterns
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
import androidx.compose.ui.layout.ContentScale
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


@Composable
fun RegisterView(navController: NavController, auth: FirebaseAuth) {

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }
    var showPass by rememberSaveable { mutableStateOf(false) }
    var showConfirm by rememberSaveable { mutableStateOf(false) }
    var passwordStrength by remember { mutableStateOf(0) }

    val context = LocalContext.current


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFEFF6FF), Color(0xFFF8FAFC))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(90.dp)
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "Crear Cuenta",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Regístrate para comenzar a monitorear el aire",
                    fontSize = 17.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 20.dp)
                )


                // correo
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it.trim() },
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(14.dp))


                // Contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordStrength = calcularFuerza(password)
                    },
                    label = { Text("Contraseña segura") },
                    placeholder = { Text("Ingresa tu contraseña") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPass = !showPass }) {
                            Icon(
                                imageVector = if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(6.dp))


                // barra de nivel de contraseña
                val colorFuerza = when (passwordStrength) {
                    1 -> Color(0xFFEF4444) // rojo
                    2 -> Color(0xFFF59E0B) // naranja
                    3 -> Color(0xFF22C55E) // verde
                    else -> Color.LightGray
                }

                val textoFuerza = when (passwordStrength) {
                    1 -> "Débil"
                    2 -> "Media"
                    3 -> "Fuerte"
                    else -> ""
                }

                if (password.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .fillMaxWidth()
                                .background(colorFuerza, RoundedCornerShape(4.dp))
                        )
                        Text(
                            text = textoFuerza,
                            fontSize = 14.sp,
                            color = colorFuerza,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))


                // confirmar contraseña
                OutlinedTextField(
                    value = confirm,
                    onValueChange = { confirm = it },
                    label = { Text("Confirmar contraseña") },
                    placeholder = { Text("Repite tu contraseña") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showConfirm = !showConfirm }) {
                            Icon(
                                imageVector = if (showConfirm) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(Modifier.height(22.dp))


                // Boton de registrar
                Button(
                    onClick = {
                        validarRegistro(
                            email,
                            password,
                            confirm,
                            auth,
                            context,
                            onSuccess = {
                                navController.popBackStack()
                                navController.navigate("login")
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                ) {
                    Text("Crear Cuenta", color = Color.White, fontWeight = FontWeight.SemiBold)
                }


                Spacer(Modifier.height(12.dp))


                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("¿Ya tienes cuenta? ", color = Color.Gray, fontSize = 17.sp)
                    Text(
                        text = "Inicia sesión",
                        color = Color(0xFF0F62FE),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable {
                                navController.popBackStack()
                                navController.navigate("login")
                            }
                            .padding(start = 4.dp)
                    )
                }

            }
        }
    }
}



//Funcion de nivel de contraseña
private fun calcularFuerza(password: String): Int {
    var fuerza = 0

    if (password.length >= 8) fuerza++
    if (password.any { it.isDigit() }) fuerza++
    if (password.any { it.isUpperCase() }) fuerza++
    if (password.any { it.isLowerCase() }) fuerza++
    if (password.any { "!@#\$%^&*(),.?\":{}|<>_-".contains(it) }) fuerza++

    return when (fuerza) {
        0, 1, 2 -> 1   // Débil
        3, 4 -> 2      // Media
        else -> 3      // Fuerte
    }
}


//validaciones manuales mas detalladas de la contraseña
private fun esPasswordSegura(password: String, email: String): String? {

    val minLength = 8
    val regexUpper = Regex(".*[A-Z].*")
    val regexLower = Regex(".*[a-z].*")
    val regexDigit = Regex(".*[0-9].*")
    val regexSpecial = Regex(".*[!@#\$%^&*(),.?\":{}|<>_-].*")

    val contraseñasComunes = listOf(
        "123456", "password", "qwerty", "123456789",
        "abc123", "111111", "123123", "admin", "root"
    )

    if (password.length < minLength)
        return "La contraseña debe tener al menos $minLength caracteres."

    if (!regexUpper.containsMatchIn(password))
        return "Debe contener al menos una letra mayúscula."

    if (!regexLower.containsMatchIn(password))
        return "Debe contener al menos una letra minúscula."

    if (!regexDigit.containsMatchIn(password))
        return "Debe contener al menos un número."

    if (!regexSpecial.containsMatchIn(password))
        return "Debe contener al menos un carácter especial."

    if (contraseñasComunes.contains(password.lowercase()))
        return "La contraseña es muy común. Usa una más segura."

    val userPart = email.substringBefore("@")
    if (password.contains(userPart, ignoreCase = true))
        return "La contraseña no debe contener tu correo."

    return null
}


//validacion de registro
private fun validarRegistro(
    email: String,
    password: String,
    confirm: String,
    auth: FirebaseAuth,
    context: Context,
    onSuccess: () -> Unit
) {

    if (email.isBlank() || password.isBlank() || confirm.isBlank()) {
        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
        return
    }

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        Toast.makeText(context, "Correo inválido", Toast.LENGTH_SHORT).show()
        return
    }

    if (password != confirm) {
        Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
        return
    }

    val error = esPasswordSegura(password, email)
    if (error != null) {
        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        return
    }

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                onSuccess()
            } else {
                Toast.makeText(
                    context,
                    "Error: ${task.exception?.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
}


