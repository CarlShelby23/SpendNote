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
import java.util.Calendar

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
    var gastoAEditar by remember { mutableStateOf<Gasto?>(null) }
    val context = LocalContext.current
    var listaGastos by remember { mutableStateOf(listOf<Gasto>()) }

    LaunchedEffect(Unit) {
        listaGastos = GastoFileManager.obtenerGastos(context)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = "Agregar") },
                    label = { Text("Agregar") },
                    selected = pantallaActual == "Agregar",
                    onClick = {
                        gastoAEditar = null
                        pantallaActual = "Agregar"
                    }
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
                    gastoAEditar = gastoAEditar,
                    onCerrarClick = {
                        gastoAEditar = null
                        pantallaActual = "Historial"
                    },
                    onGuardarClick = { montoString, categoria, idExistente ->
                        val nuevoMonto = montoString.toDoubleOrNull() ?: 0.0
                        if (idExistente == null) {
                            val nuevoGasto = Gasto(monto = nuevoMonto, categoria = categoria)
                            listaGastos = listaGastos + nuevoGasto
                        } else {
                            listaGastos = listaGastos.map {
                                if (it.id == idExistente) it.copy(monto = nuevoMonto, categoria = categoria) else it
                            }
                        }
                        GastoFileManager.guardarGastos(context, listaGastos)
                        gastoAEditar = null
                        pantallaActual = "Historial"
                    }
                )
                "Historial" -> HistorialScreen(
                    gastos = listaGastos,
                    onDeleteSemana = { fechaEnSemana ->
                        listaGastos = GastoFileManager.eliminarSemana(context, listaGastos, fechaEnSemana)
                    },
                    onEditGasto = { gasto ->
                        gastoAEditar = gasto
                        pantallaActual = "Agregar"
                    }
                )
                "Resumen" -> {
                    val hoy = System.currentTimeMillis()
                    val calHoy = Calendar.getInstance().apply { timeInMillis = hoy }
                    val totalHoy = listaGastos.filter {
                        val calGasto = Calendar.getInstance().apply { timeInMillis = it.fecha }
                        calGasto.get(Calendar.YEAR) == calHoy.get(Calendar.YEAR) &&
                                calGasto.get(Calendar.DAY_OF_YEAR) == calHoy.get(Calendar.DAY_OF_YEAR)
                    }.sumOf { it.monto }

                    val semanaActual = calHoy.get(Calendar.WEEK_OF_YEAR)
                    val totalSemana = listaGastos.filter {
                        val calGasto = Calendar.getInstance().apply { timeInMillis = it.fecha }
                        calGasto.get(Calendar.YEAR) == calHoy.get(Calendar.YEAR) &&
                                calGasto.get(Calendar.WEEK_OF_YEAR) == semanaActual
                    }.sumOf { it.monto }

                    ResumenScreen(
                        totalHoy = String.format("%.2f", totalHoy),
                        totalSemana = String.format("%.2f", totalSemana)
                    )
                }
            }
        }
    }
}