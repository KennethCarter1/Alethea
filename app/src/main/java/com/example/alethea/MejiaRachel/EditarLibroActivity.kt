package com.example.alethea.MejiaRachel

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.alethea.AletheaBd
import com.example.alethea.ImageUtil
import com.example.alethea.R

class EditarLibroActivity : AppCompatActivity() {
    private val bd by lazy { AletheaBd(this) }
    private var libroId = -1
    private var categoriaSeleccionada = ""
    private var rutaImagen: String? = null

    private val launcherImagen = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                val nombre = ImageUtil.copiarImagen(this, uri)
                if (nombre != null) {
                    rutaImagen = nombre
                    findViewById<TextView>(R.id.tvImagenLibro).text = nombre
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
        findViewById<android.view.View>(R.id.tvImagenLibro).setOnClickListener {
            launcherImagen.launch(ImageUtil.crearIntentSelector())
        }
    }

    private fun cargarDatos() {
        val db = bd.readableDatabase
        val cursor = db.rawQuery(
            "SELECT nombre_libro, autor_libro, categoria, ano_creado, sinopsis, COALESCE(ruta_imagen,''), stock FROM Libros WHERE id = ?",
            arrayOf(libroId.toString())
        )
        if (cursor.moveToFirst()) {
            findViewById<EditText>(R.id.etNombreLibro).setText(cursor.getString(0))
            findViewById<EditText>(R.id.etAutorLibro).setText(cursor.getString(1))
            categoriaSeleccionada = cursor.getString(2)
            findViewById<TextView>(R.id.tvCategoria).text = categoriaSeleccionada
            findViewById<EditText>(R.id.etAnioCreado).setText(cursor.getString(3))
            findViewById<EditText>(R.id.etSinopsis).setText(cursor.getString(4))
            findViewById<EditText>(R.id.etStock).setText(cursor.getInt(6).toString())
            val img = cursor.getString(5)
            if (img.isNotEmpty()) {
                rutaImagen = img
                findViewById<TextView>(R.id.tvImagenLibro).text = img
                val iv = findViewById<ImageView>(R.id.ivPreviewImagen)
                val archivo = java.io.File(filesDir, "images/$img")
                if (archivo.exists()) {
                    iv.setImageBitmap(BitmapFactory.decodeFile(archivo.absolutePath))
                    iv.visibility = android.view.View.VISIBLE
                }
            }
        }
        cursor.close()
        db.close()
    }

    private fun seleccionarCategoria() {
        val categorias = arrayOf("Novela", "Infantil", "Distopía", "Misterio", "Clásico", "Ciencia Ficción", "Historia", "Poesía", "Romance", "Terror", "Fantasía", "Biografía", "Ensayo")
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
        val stockTexto = findViewById<EditText>(R.id.etStock).text.toString().trim()

        if (nombre.isEmpty() || autor.isEmpty() || categoriaSeleccionada.isEmpty() || anio.isEmpty() || sinopsis.isEmpty() || stockTexto.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val stock = stockTexto.toIntOrNull()
        if (stock == null || stock < 0) {
            Toast.makeText(this, "El stock debe ser un número mayor o igual a cero", Toast.LENGTH_SHORT).show()
            return
        }

        val db = bd.writableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM Prestamos WHERE libro_id = ? AND estado = 'Aceptada'",
            arrayOf(libroId.toString())
        )
        val prestamosActivos = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        if (stock < prestamosActivos) {
            db.close()
            Toast.makeText(this, "El stock no puede ser menor que los $prestamosActivos préstamos activos", Toast.LENGTH_LONG).show()
            return
        }
        db.execSQL(
            "UPDATE Libros SET nombre_libro = ?, autor_libro = ?, categoria = ?, ano_creado = ?, sinopsis = ?, ruta_imagen = ?, stock = ? WHERE id = ?",
            arrayOf<Any?>(nombre, autor, categoriaSeleccionada, anio, sinopsis, rutaImagen, stock, libroId)
        )
        db.close()
        Toast.makeText(this, "Libro actualizado", Toast.LENGTH_SHORT).show()
        finish()
    }
}
