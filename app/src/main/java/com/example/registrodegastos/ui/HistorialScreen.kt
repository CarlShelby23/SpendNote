package com.example.registrodegastos.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.registrodegastos.data.GastoFileManager
import com.example.registrodegastos.model.Gasto
import java.util.Calendar

@Composable
fun HistorialScreen(
    gastos: List<Gasto>,
    onDeleteSemana: (Long) -> Unit,
    onEditGasto: (Gasto) -> Unit
) {
    val grupos = gastos.groupBy {
        val cal = Calendar.getInstance().apply { timeInMillis = it.fecha }
        cal.get(Calendar.WEEK_OF_YEAR)
    }.toSortedMap(compareByDescending { it })

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        grupos.forEach { (_, listaSemanal) ->
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Semana: ${GastoFileManager.obtenerRangoSemana(listaSemanal.first().fecha)}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = { onDeleteSemana(listaSemanal.first().fecha) }) {
                        Icon(Icons.Default.Delete, "Borrar", tint = Color.Red, modifier = Modifier.size(20.dp))
                    }
                }
            }
            items(listaSemanal) { gasto ->
                GastoItem(gasto = gasto, onEditClick = { onEditGasto(gasto) })
            }
        }
    }
}

@Composable
fun GastoItem(gasto: Gasto, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(gasto.categoria.take(1).uppercase(), fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(gasto.categoria, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(GastoFileManager.formatearFecha(gasto.fecha), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("$${String.format("%.2f", gasto.monto)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(20.dp), tint = Color.Gray)
                }
            }
        }
    }
}