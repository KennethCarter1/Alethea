package com.example.alethea.DelgadoJhezrrel

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.alethea.R

class AgregarLibrosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_jhezrrel_agregar_libros)
        supportActionBar?.hide()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<android.widget.ImageView>(R.id.btnVolver).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnCancelar).setOnClickListener { finish() }

        val categorias = listOf("Novela", "Historia", "Ciencia", "Tecnologia", "Poesia")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categorias)
        findViewById<Spinner>(R.id.spCategoria).adapter = adapter

        findViewById<TextView>(R.id.tvImagen).setOnClickListener {
            Toast.makeText(this, "Selector de imagen pendiente de integrar", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            val nombre = findViewById<EditText>(R.id.etNombreLibro).text.toString().trim()
            val autor = findViewById<EditText>(R.id.etAutorLibro).text.toString().trim()
            val anio = findViewById<EditText>(R.id.etAnio).text.toString().trim()
            val sinopsis = findViewById<EditText>(R.id.etSinopsis).text.toString().trim()

            if (nombre.isEmpty() || autor.isEmpty() || anio.isEmpty() || sinopsis.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Libro guardado: $nombre", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}
