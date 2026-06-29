package com.example.alethea.MejiaRachel

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alethea.R

class GestionLibrosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rachel_gestion_libros)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- Conexion del RecyclerView (datos de prueba, luego van de la BD) ---
        val rvLibros = findViewById<RecyclerView>(R.id.rvLibros)
        rvLibros.layoutManager = LinearLayoutManager(this)

        val listaDePrueba = listOf(
            Libro("El Principito", "Antoine de Saint-Exupéry", "Infantil", "1943"),
            Libro("1984", "George Orwell", "Ficción", "1949"),
            Libro("Cien Años de Soledad", "Gabriel García Márquez", "Realismo mágico", "1967")
        )

        rvLibros.adapter = LibroAdapter(listaDePrueba)

        findViewById<android.widget.ImageView>(R.id.btnAtras).setOnClickListener { finish() }
    }
}