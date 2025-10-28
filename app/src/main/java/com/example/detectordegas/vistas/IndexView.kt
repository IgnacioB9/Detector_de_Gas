package com.example.detectordegas.vistas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun IndexView(
    navController: NavController,
    auth: FirebaseAuth,
    onLogout: () -> Unit = {}
) {
    var connected by remember { mutableStateOf(true) }
    var gasValue by remember { mutableStateOf(0f) }

    // Simulación funcionamiento
    LaunchedEffect(connected) {
        while (connected) {
            delay(2000)
            gasValue = Random.nextFloat() * 1000f // Simula el valor del sensor y entrega valores entre 0 y 1000
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detector GLP", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = {
                        auth.signOut() // Cierra sesión en Firebase
                        navController.navigate("login") {
                            popUpTo("index") { inclusive = true } // Elimina la vista actual y vuelve a la ruta indicada
                        }
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión")
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // simulacion del estado de conexión WiFi
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            if (connected) "WiFi Conectado" else "WiFi Desconectado",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            if (connected) "Recibiendo datos del sensor..." else "Esperando conexión WiFi",
                            fontSize = 14.sp
                        )
                    }
                    Icon(
                        imageVector = if (connected) Icons.Default.Wifi else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (connected) Color(0xFF22C55E) else Color(0xFFEF4444),
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Lectura de GLP
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Concentración de GLP", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(16.dp))

                    // Valor del sensor entregados por el random antes usado
                    Text(
                        String.format("%.2f ppm", gasValue),
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            gasValue < 200 -> Color(0xFF22C55E) // Seguro
                            gasValue < 600 -> Color(0xFFFACC15) // Precaución
                            else -> Color(0xFFEF4444) // Peligroso
                        }
                    )

                    Spacer(Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = (gasValue / 1000f).coerceIn(0f, 1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        color = when {
                            gasValue < 200 -> Color(0xFF22C55E)
                            gasValue < 600 -> Color(0xFFFACC15)
                            else -> Color(0xFFEF4444)
                        }
                    )

                    Spacer(Modifier.height(12.dp))
                    Text(
                        when {
                            gasValue < 200 -> "Nivel Seguro"
                            gasValue < 600 -> "Nivel Moderado – Ventila el área"
                            else -> "Nivel Peligroso – Riesgo de explosión"
                        },
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            // Estado visual del sensor
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Tipo de Gas", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "GLP (Gas Licuado)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0EA5E9)
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Indicador circular del estado general
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            !connected -> Color.LightGray
                            gasValue < 200 -> Color(0xFF22C55E)
                            gasValue < 600 -> Color(0xFFFACC15)
                            else -> Color(0xFFEF4444)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (connected) "ON" else "OFF",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


