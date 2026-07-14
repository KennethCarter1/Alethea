package com.example.alethea.CarterKenneth

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.alethea.AletheaBd
import com.example.alethea.R
import com.example.alethea.SessionManager
import java.io.File

class CatalogoLibrosActivity : AppCompatActivity() {
    private var todosLosLibros = listOf<LibroCat>()
    private var generos = listOf("Todos")
    private var autores = listOf("Todos")
    private var anios = listOf("Todos")
    private var generoSeleccionado = "Todos"
    private var autorSeleccionado = "Todos"
    private var anioSeleccionado = "Todos"

    data class LibroCat(
        val id: Int, val titulo: String, val autor: String,
        val categoria: String, val anio: String, val rutaImagen: String,
        val stock: Int, val disponibles: Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kenneth_catalogo_libros)

        findViewById<ImageView>(R.id.btnAtras).setOnClickListener { finish() }

        cargarFiltros()
        cargarLibros()

        findViewById<EditText>(R.id.etBuscarCat).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = filtrar()
            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun cargarFiltros() {
        val db = AletheaBd(this).readableDatabase

        val cGen = db.rawQuery("SELECT DISTINCT COALESCE(categoria,'') FROM Libros ORDER BY categoria", null)
        val lGen = mutableListOf("Todos")
        while (cGen.moveToNext()) { val g = cGen.getString(0); if (g.isNotEmpty()) lGen.add(g) }
        cGen.close()
        generos = lGen

        val cAut = db.rawQuery("SELECT DISTINCT COALESCE(autor_libro,'') FROM Libros ORDER BY autor_libro", null)
        val lAut = mutableListOf("Todos")
        while (cAut.moveToNext()) { val a = cAut.getString(0); if (a.isNotEmpty()) lAut.add(a) }
        cAut.close()
        autores = lAut

        val cAnio = db.rawQuery("SELECT DISTINCT COALESCE(ano_creado,'') FROM Libros ORDER BY ano_creado", null)
        val lAnio = mutableListOf("Todos")
        while (cAnio.moveToNext()) { val a = cAnio.getString(0); if (a.isNotEmpty()) lAnio.add(a) }
        cAnio.close()
        db.close()
        anios = lAnio

        fun setupSpinner(id: Int, items: List<String>, onSelect: (String) -> Unit) {
            val sp = findViewById<Spinner>(id)
            sp.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            sp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p: AdapterView<*>?, v: android.view.View?, pos: Int, id: Long) {
                    onSelect(items[pos]); filtrar()
                }
                override fun onNothingSelected(p: AdapterView<*>?) {}
            }
        }

        setupSpinner(R.id.spinnerGeneroCat, generos) { generoSeleccionado = it }
        setupSpinner(R.id.spinnerAutorCat, autores) { autorSeleccionado = it }
        setupSpinner(R.id.spinnerAnioCat, anios) { anioSeleccionado = it }
    }

    private fun cargarLibros() {
        val db = AletheaBd(this).readableDatabase
        val c = db.rawQuery(
            """SELECT id, nombre_libro, COALESCE(autor_libro,''), COALESCE(categoria,''),
                      COALESCE(ano_creado,''), COALESCE(ruta_imagen,''), stock,
                      MAX(stock - (SELECT COUNT(*) FROM Prestamos p
                          WHERE p.libro_id = Libros.id AND p.estado = 'Aceptada'), 0)
               FROM Libros ORDER BY nombre_libro""", null
        )
        todosLosLibros = mutableListOf()
        while (c.moveToNext()) {
            (todosLosLibros as MutableList).add(
                LibroCat(
                    c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
                    c.getString(4), c.getString(5), c.getInt(6), c.getInt(7)
                )
            )
        }
        c.close()
        db.close()
        filtrar()
    }

    private fun filtrar() {
        val query = findViewById<EditText>(R.id.etBuscarCat).text.toString().trim().lowercase()
        val filtrados = todosLosLibros.filter { libro ->
            (generoSeleccionado == "Todos" || libro.categoria == generoSeleccionado) &&
            (autorSeleccionado == "Todos" || libro.autor == autorSeleccionado) &&
            (anioSeleccionado == "Todos" || libro.anio == anioSeleccionado) &&
            (query.isEmpty() || libro.titulo.lowercase().contains(query) || libro.autor.lowercase().contains(query))
        }
        findViewById<TextView>(R.id.tvCountCat).text = "${filtrados.size} libro(s)"
        renderLibros(filtrados)
    }

    private fun renderLibros(items: List<LibroCat>) {
        val container = findViewById<LinearLayout>(R.id.listaCatalogo)
        container.removeAllViews()
        val usuarioId = SessionManager.getUsuarioId()
        val inflater = LayoutInflater.from(this)

        if (items.isEmpty()) {
            val empty = TextView(this)
            empty.text = "No se encontraron libros"
            empty.setTextColor(ContextCompat.getColor(this, R.color.kenneth_texto_vacio))
            empty.textSize = 14f
            empty.gravity = android.view.Gravity.CENTER
            empty.setPadding(0, 40, 0, 0)
            container.addView(empty)
            return
        }

        items.forEach { libro ->
            val item = inflater.inflate(R.layout.item_catalogo, container, false)
            item.findViewById<TextView>(R.id.tvTituloCat).text = libro.titulo
            item.findViewById<TextView>(R.id.tvAutorCat).text = libro.autor
            item.findViewById<TextView>(R.id.tvCategoriaCat).text = libro.categoria
            item.findViewById<TextView>(R.id.tvAnioCat).text = libro.anio
            val tvStock = item.findViewById<TextView>(R.id.tvStockCat)
            if (libro.disponibles == 0) {
                tvStock.text = getString(R.string.ken_sin_stock)
                tvStock.setBackgroundResource(R.drawable.kenneth_badge_sin_stock)
                tvStock.setTextColor(ContextCompat.getColor(this, R.color.kenneth_sin_stock_texto))
                tvStock.setPadding(10, 4, 10, 4)
            } else {
                tvStock.text = getString(R.string.ken_disponibles_formato, libro.disponibles, libro.stock)
                tvStock.background = null
                tvStock.setTextColor(ContextCompat.getColor(this, R.color.kenneth_texto_principal))
            }

            if (libro.rutaImagen.isNotEmpty()) {
                val archivo = File(filesDir, "images/${libro.rutaImagen}")
                if (archivo.exists()) {
                    item.findViewById<ImageView>(R.id.ivPortadaCat).setImageBitmap(
                        BitmapFactory.decodeFile(archivo.absolutePath)
                    )
                }
            }

            if (usuarioId != -1) {
                val db = AletheaBd(this).readableDatabase
                val c = db.rawQuery("SELECT id FROM Favoritos WHERE usuario_id = ? AND libro_id = ?",
                    arrayOf(usuarioId.toString(), libro.id.toString()))
                if (c.moveToFirst()) {
                    item.findViewById<ImageView>(R.id.ivFavCat).setColorFilter(
                        ContextCompat.getColor(this, R.color.kenneth_favorito)
                    )
                }
                c.close()
                db.close()
            }

            item.setOnClickListener {
                val intent = Intent(this, DetalleLibroActivity::class.java)
                intent.putExtra("titulo", libro.titulo)
                startActivity(intent)
            }

            item.findViewById<android.view.View>(R.id.btnFavCat).setOnClickListener {
                if (usuarioId == -1) {
                    Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val dbw = AletheaBd(this).writableDatabase
                val cc = dbw.rawQuery("SELECT id FROM Favoritos WHERE usuario_id = ? AND libro_id = ?",
                    arrayOf(usuarioId.toString(), libro.id.toString()))
                val existe = cc.moveToFirst()
                cc.close()
                val ivFav = item.findViewById<ImageView>(R.id.ivFavCat)
                if (existe) {
                    dbw.execSQL("DELETE FROM Favoritos WHERE usuario_id = ? AND libro_id = ?",
                        arrayOf(usuarioId.toString(), libro.id.toString()))
                    ivFav.clearColorFilter()
                    Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                } else {
                    dbw.execSQL("INSERT INTO Favoritos(usuario_id, libro_id) VALUES(?, ?)",
                        arrayOf(usuarioId.toString(), libro.id.toString()))
                    ivFav.setColorFilter(ContextCompat.getColor(this, R.color.kenneth_favorito))
                    Toast.makeText(this, "Añadido a favoritos", Toast.LENGTH_SHORT).show()
                }
                dbw.close()
            }

            container.addView(item)
        }
    }
}
