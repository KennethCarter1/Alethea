package com.example.alethea.MejiaRachel

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.alethea.AletheaBd
import com.example.alethea.R

class EditarLibroActivity : AppCompatActivity() {
    private val bd by lazy { AletheaBd(this) }
    private var libroId = -1
    private var categoriaSeleccionada = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rachel_editar_libro)

        findViewById<android.widget.ImageView>(R.id.btnAtras).setOnClickListener { finish() }

        libroId = intent.getIntExtra("libro_id", -1)
        if (libroId == -1) {
            Toast.makeText(this, "Libro no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        cargarDatos()
        findViewById<android.view.View>(R.id.tvCategoria).setOnClickListener { seleccionarCategoria() }
        findViewById<android.view.View>(R.id.btnCancelar).setOnClickListener { finish() }
        findViewById<android.view.View>(R.id.btnEditarLibro).setOnClickListener { guardarCambios() }
    }

    private fun cargarDatos() {
        val db = bd.readableDatabase
        val cursor = db.rawQuery(
            "SELECT nombre_libro, autor_libro, categoria, ano_creado, sinopsis FROM Libros WHERE id = ?",
            arrayOf(libroId.toString())
        )
        if (cursor.moveToFirst()) {
            findViewById<EditText>(R.id.etNombreLibro).setText(cursor.getString(0))
            findViewById<EditText>(R.id.etAutorLibro).setText(cursor.getString(1))
            categoriaSeleccionada = cursor.getString(2)
            findViewById<TextView>(R.id.tvCategoria).text = categoriaSeleccionada
            findViewById<EditText>(R.id.etAnioCreado).setText(cursor.getString(3))
            findViewById<EditText>(R.id.etSinopsis).setText(cursor.getString(4))
        }
        cursor.close()
        db.close()
    }

    private fun seleccionarCategoria() {
        val categorias = arrayOf("Ficción", "No Ficción", "Ciencia Ficción", "Fantástica", "Romance", "Terror", "Misterio", "Historia", "Biografía", "Infantil", "Juvenil", "Poesía", "Ensayo")
        AlertDialog.Builder(this)
            .setTitle("Seleccionar categoría")
            .setItems(categorias) { _, which ->
                categoriaSeleccionada = categorias[which]
                findViewById<TextView>(R.id.tvCategoria).text = categoriaSeleccionada
            }
            .show()
    }

    private fun guardarCambios() {
        val nombre = findViewById<EditText>(R.id.etNombreLibro).text.toString().trim()
        val autor = findViewById<EditText>(R.id.etAutorLibro).text.toString().trim()
        val anio = findViewById<EditText>(R.id.etAnioCreado).text.toString().trim()
        val sinopsis = findViewById<EditText>(R.id.etSinopsis).text.toString().trim()

        if (nombre.isEmpty() || autor.isEmpty() || categoriaSeleccionada.isEmpty() || anio.isEmpty() || sinopsis.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val db = bd.writableDatabase
        db.execSQL(
            "UPDATE Libros SET nombre_libro = ?, autor_libro = ?, categoria = ?, ano_creado = ?, sinopsis = ? WHERE id = ?",
            arrayOf(nombre, autor, categoriaSeleccionada, anio, sinopsis, libroId.toString())
        )
        db.close()
        Toast.makeText(this, "Libro actualizado", Toast.LENGTH_SHORT).show()
        finish()
    }
}