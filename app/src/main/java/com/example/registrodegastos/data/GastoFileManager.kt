package com.example.registrodegastos.data

import android.content.Context
import com.example.registrodegastos.model.Gasto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object GastoFileManager {
    private const val FILE_NAME = "gastos.json"

    fun guardarGastos(context: Context, lista: List<Gasto>) {
        val json = Gson().toJson(lista)
        context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }

    fun obtenerGastos(context: Context): List<Gasto> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return emptyList()
        return Gson().fromJson(file.readText(), object : TypeToken<List<Gasto>>() {}.type)
    }

    fun formatearFecha(timestamp: Long): String =
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(timestamp))

    fun obtenerRangoSemana(timestamp: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        val inicio = SimpleDateFormat("dd MMM", Locale.getDefault()).format(cal.time)
        cal.add(Calendar.DAY_OF_WEEK, 6)
        val fin = SimpleDateFormat("dd MMM", Locale.getDefault()).format(cal.time)
        return "$inicio - $fin"
    }

    fun eliminarSemana(context: Context, gastos: List<Gasto>, fechaEnSemana: Long): List<Gasto> {
        val calBusqueda = Calendar.getInstance().apply { timeInMillis = fechaEnSemana }
        val semanaBusqueda = calBusqueda.get(Calendar.WEEK_OF_YEAR)
        val nuevaLista = gastos.filter {
            val calGasto = Calendar.getInstance().apply { timeInMillis = it.fecha }
            calGasto.get(Calendar.WEEK_OF_YEAR) != semanaBusqueda
        }
        guardarGastos(context, nuevaLista)
        return nuevaLista
    }
}