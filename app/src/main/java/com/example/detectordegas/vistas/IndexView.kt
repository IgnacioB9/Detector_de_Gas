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
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndexView(
    navController: NavController,
    auth: FirebaseAuth
) {
    var connected by remember { mutableStateOf(false) }
    var gasValue by remember { mutableStateOf(0f) }

    // --- Escuchar datos en tiempo real desde Firebase ---
    LaunchedEffect(Unit) {
        val db = FirebaseDatabase.getInstance().getReference("sensor")

        db.child("gas").onValue { value ->
            gasValue = value?.toString()?.toFloatOrNull() ?: 0f
        }

        db.child("wifi").onValue { value ->
            connected = value == "online"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detector GLP", fontSize = 25.sp, fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(
                        onClick = {
                            auth.signOut()

                            // 👉 Volver al LoginView y limpiar navegación
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    ) {

                    }
                }
            )
        }, bottomBar = {
            BottomNavBar(navController)
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

            // Estado WiFi
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
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
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            if (connected) "Recibiendo datos del sensor..." else "Esperando conexión...",
                            fontSize = 18.sp
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

            // Lectura GLP
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Concentración de GLP", fontSize = 20.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(16.dp))

                    Text(
                        String.format("%.2f ppm", gasValue),
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            gasValue < 200 -> Color(0xFF22C55E)
                            gasValue < 600 -> Color(0xFFFACC15)
                            else -> Color(0xFFEF4444)
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
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Indicador ON/OFF
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(if (connected) Color(0xFF1BE514) else Color(0xFFEF4444)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (connected) "ON" else "OFF",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/* --- Extensión Firebase para obtener datos --- */
fun com.google.firebase.database.DatabaseReference.onValue(callback: (Any?) -> Unit) {
    this.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
        override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
            callback(snapshot.value)
        }
        override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
    })
}



