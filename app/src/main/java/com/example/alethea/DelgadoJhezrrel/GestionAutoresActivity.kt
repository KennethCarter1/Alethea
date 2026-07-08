package com.example.alethea.DelgadoJhezrrel

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.alethea.AletheaBd
import com.example.alethea.R

class GestionAutoresActivity : AppCompatActivity() {
    private val bd by lazy { AletheaBd(this) }
    private var autores = listOf<Autor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_jhezrrel_gestion_autores)
        supportActionBar?.hide()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<android.widget.ImageView>(R.id.btnVolver).setOnClickListener { finish() }
        cargarAutores()
        findViewById<EditText>(R.id.etBuscarAutor).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filtro = s.toString().trim().lowercase()
                renderAutores(autores.filter { it.nombre.lowercase().contains(filtro) })
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun cargarAutores() {
        autores = mutableListOf()
        val db = bd.readableDatabase
        val cursor = db.rawQuery("SELECT DISTINCT autor_libro FROM Libros ORDER BY autor_libro", null)
        while (cursor.moveToNext()) {
            val nombre = cursor.getString(0)
            val cursorCount = db.rawQuery("SELECT COUNT(*) FROM Libros WHERE autor_libro = ?", arrayOf(nombre))
            val count = if (cursorCount.moveToFirst()) cursorCount.getInt(0) else 0
            cursorCount.close()
            (autores as MutableList).add(Autor(nombre, count))
        }
        cursor.close()
        db.close()

        findViewById<TextView>(R.id.tvTotalAutores).text = autores.size.toString().padStart(2, '0')
        findViewById<TextView>(R.id.tvTotalLibros).text = autores.sumOf { it.libros }.toString()
        renderAutores(autores)
    }

    private fun renderAutores(items: List<Autor>) {
        val lista = findViewById<LinearLayout>(R.id.listaAutores)
        lista.removeAllViews()
        items.forEach { autor ->
            lista.addView(TextView(this).apply {
                text = autor.nombre
                textSize = 24f
                setTextColor(getColor(R.color.negro_suave))
                setPadding(8, 8, 8, 0)
            })
            lista.addView(TextView(this).apply {
                text = "${autor.libros} libros"
                textSize = 15f
                setTextColor(getColor(R.color.gris_medio))
                setPadding(8, 0, 8, 8)
            })
            lista.addView(View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
                ).apply {
                    setMargins(8, 8, 8, 14)
                }
                setBackgroundColor(getColor(R.color.gris_medio))
            })
        }
    }

    data class Autor(val nombre: String, val libros: Int)
}