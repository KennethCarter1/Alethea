package com.example.alethea.DelgadoJhezrrel

import android.content.ContentValues
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.alethea.AletheaBd
import com.example.alethea.ImageUtil
import com.example.alethea.R

class AgregarLibrosActivity : AppCompatActivity() {
    private var rutaImagen: String? = null

    private val launcherImagen = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                val nombre = ImageUtil.copiarImagen(this, uri)
                if (nombre != null) {
                    rutaImagen = nombre
                    findViewById<TextView>(R.id.tvImagen).text = nombre
                    val iv = findViewById<ImageView>(R.id.ivPreviewImagen)
                    val archivo = java.io.File(filesDir, "images/$nombre")
                    if (archivo.exists()) {
                        iv.setImageBitmap(BitmapFactory.decodeFile(archivo.absolutePath))
                        iv.visibility = android.view.View.VISIBLE
                    }
                } else {
                    Toast.makeText(this, "Error al copiar la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

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

        val bd = AletheaBd(this)

        findViewById<android.widget.ImageView>(R.id.btnVolver).setOnClickListener { finish() }
        findViewById<LinearLayout>(R.id.btnCancelar).setOnClickListener { finish() }

        val categorias = listOf("Novela", "Infantil", "Distopía", "Misterio", "Clásico", "Ciencia Ficción", "Historia", "Poesía", "Romance", "Terror", "Fantasía", "Biografía", "Ensayo")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categorias)
        findViewById<Spinner>(R.id.spCategoria).adapter = adapter

        findViewById<TextView>(R.id.tvImagen).setOnClickListener {
            launcherImagen.launch(ImageUtil.crearIntentSelector())
        }

        findViewById<LinearLayout>(R.id.btnGuardar).setOnClickListener {
            val nombre = findViewById<EditText>(R.id.etNombreLibro).text.toString().trim()
            val autor = findViewById<EditText>(R.id.etAutorLibro).text.toString().trim()
            val categoria = findViewById<Spinner>(R.id.spCategoria).selectedItem.toString()
            val anio = findViewById<EditText>(R.id.etAnio).text.toString().trim()
            val stockTexto = findViewById<EditText>(R.id.etStock).text.toString().trim()
            val sinopsis = findViewById<EditText>(R.id.etSinopsis).text.toString().trim()

            if (nombre.isEmpty() || autor.isEmpty() || anio.isEmpty() || stockTexto.isEmpty() || sinopsis.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val stock = stockTexto.toIntOrNull()
            if (stock == null || stock < 0) {
                Toast.makeText(this, "El stock debe ser un número válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = bd.writableDatabase
            val valores = ContentValues().apply {
                put("nombre_libro", nombre)
                put("autor_libro", autor)
                put("categoria", categoria)
                put("ano_creado", anio)
                put("sinopsis", sinopsis)
                put("ruta_imagen", rutaImagen)
                put("stock", stock)
            }
            val resultado = db.insert("Libros", null, valores)
            db.close()

            if (resultado == -1L) {
                Toast.makeText(this, "Error al guardar el libro", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Libro guardado: $nombre", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
