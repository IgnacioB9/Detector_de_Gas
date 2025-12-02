package com.example.detectordegas.vistas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndexView(
    navController: NavController,
    auth: FirebaseAuth
) {
    var gasTemp by remember { mutableStateOf(0f) } // dato almacenado
    var connected by remember { mutableStateOf(false) }
    var gasValue by remember { mutableStateOf(0f) } // dato cada 10 s
    var hasData by remember { mutableStateOf(false) }
    // Buzzer
    var buzzerOn by remember { mutableStateOf(false) }

    //timestamp de última lectura
    var lastUpdate by remember { mutableStateOf(0L) }

    //////////////////////////////////////////////////////////////////////

    // --- Lectura en tiempo real desde Firebase ---
    LaunchedEffect(Unit) {
        val db = FirebaseDatabase.getInstance().getReference("sensor")

        db.child("gas").onValue { value ->
            lastUpdate = System.currentTimeMillis()
            hasData = value != null
            gasTemp = value.toString().toFloatOrNull() ?: 0f    // Solo se almacena
        }

        db.child("wifi").onValue { value ->
            connected = value == "online"
            lastUpdate = System.currentTimeMillis()
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            gasValue = gasTemp        // último valor recibido
            kotlinx.coroutines.delay(10_000)
        }
    }
////////////////////////////////////////////////////////////////////

    //verifica la inactividad del sensor
    LaunchedEffect(Unit) {
        while (true) {
            val diff = System.currentTimeMillis() - lastUpdate

            if (diff > 15000) { // 10 segundos sin datos
                connected = false
                hasData = false
            }

            kotlinx.coroutines.delay(3000)
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
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    ) {}
                }
            )
        }, bottomBar = { BottomNavBar(navController) }
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
                            if (connected) "Recibiendo datos del sensor..."
                            else "Sin conexión / ESP32 apagado",
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
                        if (connected && hasData) String.format("%.2f ppm", gasValue) else "-- ppm",
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            !connected || !hasData -> Color.Gray
                            gasValue < 200 -> Color(0xFF22C55E)
                            gasValue < 600 -> Color(0xFFFACC15)
                            else -> Color(0xFFEF4444)
                        }
                    )

                    Spacer(Modifier.height(16.dp))

                    if (connected && hasData) {
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
                    } else {
                        Text("Sin datos del sensor", fontSize = 16.sp, color = Color.Gray)
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        when {
                            !connected || !hasData -> "Sin lectura disponible"
                            gasValue < 200 -> "Nivel Seguro"
                            gasValue <= 400 -> "Nivel Moderado – Ventila el área"
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
                    .background(if (buzzerOn) Color(0xFF1BE514) else Color(0xFFEF4444))
                    .clickable {
                        buzzerOn = !buzzerOn  // Cambia visualmente

                        // 🔥 ENVIAR COMANDO DESDE ANDROID A ESP32 USANDO FIREBASE
                        val db = FirebaseDatabase.getInstance().getReference("sensor")

                        if (buzzerOn) {
                            // Buzzer activado por 10 segundos
                            db.child("buzzer").setValue("on_10000")
                        } else {
                            // Buzzer apagado manualmente
                            db.child("buzzer").setValue("off")
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (buzzerOn) "ON" else "OFF",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


fun com.google.firebase.database.DatabaseReference.onValue(callback: (Any?) -> Unit) {
    this.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
        override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
            callback(snapshot.value)
        }
        override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
    })
}


