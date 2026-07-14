package com.example.alethea.MejiaRachel

data class Libro(
    val id: Int = 0,
    val nombre: String,
    val autor: String,
    val categoria: String,
    val anio: String,
    val sinopsis: String = "",
    val rutaImagen: String = "",
    val stock: Int = 0,
    val disponibles: Int = 0
)
