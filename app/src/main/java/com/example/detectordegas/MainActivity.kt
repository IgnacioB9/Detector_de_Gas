package com.example.detectordegas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.detectordegas.vistas.LoginView
import com.example.detectordegas.vistas.RegisterView
import com.example.detectordegas.ui.theme.DetectorDeGasTheme
import com.example.detectordegas.vistas.IndexView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //inicializacion de firebase
            auth = Firebase.auth
            DetectorDeGasTheme {

                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {

                        NavHost(
                            navController = navController,
                            startDestination = "login"
                        ){
                            composable("login") {
                                LoginView(navController, auth)
                            }
                            composable("register") {
                                RegisterView(navController, auth)
                            }
                            composable("index") {
                                IndexView(navController = navController,auth,
                                    onLogout = {
                                        navController.navigate("login"){
                                            popUpTo("index"){inclusive = true}
                                        }
                                    })
                            }
                        }

                    }
                }
            }
        }
    }
}

