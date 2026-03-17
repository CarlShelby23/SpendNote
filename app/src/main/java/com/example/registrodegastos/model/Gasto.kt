package com.example.registrodegastos.model

import java.util.UUID

data class Gasto(
    val id: String = UUID.randomUUID().toString(),
    val cantidad: Double,
    val categoria: String,
    val fecha: Long = System.currentTimeMillis()
)