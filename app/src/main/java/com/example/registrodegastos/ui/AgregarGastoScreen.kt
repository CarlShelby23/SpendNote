package com.example.registrodegastos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.registrodegastos.model.Gasto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarGastoScreen(
    gastoAEditar: Gasto? = null,
    onCerrarClick: () -> Unit,
    onGuardarClick: (String, String, String?) -> Unit
) {
    var cantidad by remember(gastoAEditar) { mutableStateOf(gastoAEditar?.monto?.toString() ?: "") }

    val categoriasLista = listOf("Café", "Transporte", "Cine", "Snacks", "Despensa", "Pago de Servicios")

    var categoriaSeleccionada by remember(gastoAEditar) {
        val cat = gastoAEditar?.categoria ?: ""
        mutableStateOf(if (cat.isNotEmpty() && cat !in categoriasLista) "Otros" else cat)
    }

    var otraCategoria by remember(gastoAEditar) {
        val cat = gastoAEditar?.categoria ?: ""
        mutableStateOf(if (cat.isNotEmpty() && cat !in categoriasLista) cat else "")
    }

    var expandido by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (gastoAEditar == null) "Agregar Gasto" else "Editar Gasto") },
                navigationIcon = {
                    IconButton(onClick = onCerrarClick) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            OutlinedTextField(
                value = cantidad,
                onValueChange = { cantidad = it },
                label = { Text("Cantidad") },
                leadingIcon = { Text("$") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = !expandido }) {
                OutlinedTextField(
                    value = if (categoriaSeleccionada.isEmpty()) "Seleccione..." else categoriaSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
                    (categoriasLista + "Otros").forEach { seleccion ->
                        DropdownMenuItem(
                            text = { Text(seleccion) },
                            onClick = { categoriaSeleccionada = seleccion; expandido = false }
                        )
                    }
                }
            }

            if (categoriaSeleccionada == "Otros") {
                OutlinedTextField(
                    value = otraCategoria,
                    onValueChange = { otraCategoria = it },
                    label = { Text("Descripción personalizada") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val catFinal = if (categoriaSeleccionada == "Otros") otraCategoria else categoriaSeleccionada
                    onGuardarClick(cantidad, catFinal, gastoAEditar?.id)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = cantidad.isNotEmpty() && categoriaSeleccionada.isNotEmpty()
            ) {
                Text(if (gastoAEditar == null) "GUARDAR GASTO" else "ACTUALIZAR GASTO")
            }
        }
    }
}