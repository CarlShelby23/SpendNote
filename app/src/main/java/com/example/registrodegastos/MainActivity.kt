package com.example.registrodegastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.registrodegastos.data.GastoFileManager
import com.example.registrodegastos.model.Gasto
import com.example.registrodegastos.ui.AgregarGastoScreen
import com.example.registrodegastos.ui.HistorialScreen
import com.example.registrodegastos.ui.ResumenScreen
import com.example.registrodegastos.ui.theme.RegistroDeGastosTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegistroDeGastosTheme {
                AppPrincipal()
            }
        }
    }
}

@Composable
fun AppPrincipal() {
    var pantallaActual by remember { mutableStateOf("Agregar") }
    val context = LocalContext.current
    val fileManager = remember { GastoFileManager() }
    var listaGastos by remember { mutableStateOf(listOf<Gasto>()) }
    LaunchedEffect(Unit) {
        listaGastos = fileManager.leerGastos(context)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = "Agregar") },
                    label = { Text("Agregar") },
                    selected = pantallaActual == "Agregar",
                    onClick = { pantallaActual = "Agregar" }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Historial") },
                    label = { Text("Historial") },
                    selected = pantallaActual == "Historial",
                    onClick = { pantallaActual = "Historial" }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = "Resumen") },
                    label = { Text("Resumen") },
                    selected = pantallaActual == "Resumen",
                    onClick = { pantallaActual = "Resumen" }
                )
            }
        }
    ) { paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues)) {
            when (pantallaActual) {
                "Agregar" -> AgregarGastoScreen(
                    onCerrarClick = {},
                    onGuardarClick = { montoString, categoria ->
                        val nuevoMonto = montoString.toDoubleOrNull() ?: 0.0
                        val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                        val nuevoGasto = Gasto(
                            id = System.currentTimeMillis().toString(),
                            monto = nuevoMonto,
                            categoria = categoria,
                            fecha = System.currentTimeMillis()
                        )

                        val nuevaLista = listaGastos + nuevoGasto
                        listaGastos = nuevaLista
                        fileManager.guardarGastos(context, nuevaLista)
                        pantallaActual = "Historial"
                    }
                )
                "Historial" -> HistorialScreen(
                    gastos = listaGastos,
                    onBackClick = { pantallaActual = "Agregar" }
                )
                "Resumen" -> {
                    val totalGasto = listaGastos.sumOf { it.monto }
                    
                    ResumenScreen(
                        totalHoy = totalGasto.toString(),
                        totalSemana = "0.00",
                        onBackClick = { pantallaActual = "Historial" }
                    )
                }
            }
        }
    }
}