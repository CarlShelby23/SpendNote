package com.example.registrodegastos.model

data class Gasto(
    val id: String = java.util.UUID.randomUUID().toString(),
    val monto: Double,
    val categoria: String,
    val fecha: Long = System.currentTimeMillis()
)