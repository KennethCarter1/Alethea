package com.example.alethea.DelgadoJhezrrel

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.alethea.AletheaBd
import com.example.alethea.R

class GestionAutoresActivity : AppCompatActivity() {
    private val bd by lazy { AletheaBd(this) }
    private var autores = listOf<Autor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jhezrrel_gestion_autores)
        supportActionBar?.hide()

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
        val container = findViewById<LinearLayout>(R.id.listaAutores)
        container.removeAllViews()
        val inflater = LayoutInflater.from(this)

        items.forEach { autor ->
            val item = inflater.inflate(R.layout.item_autor_gestion, container, false)
            item.findViewById<TextView>(R.id.tvNombre).text = autor.nombre
            item.findViewById<TextView>(R.id.tvLibros).text = "${autor.libros} libro${if (autor.libros != 1) "s" else ""}"
            container.addView(item)
        }
    }

    data class Autor(val nombre: String, val libros: Int)
}
