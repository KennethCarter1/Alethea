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

class FavoritosActivity : AppCompatActivity() {
    private var librosFav = listOf<FavLibro>()
    private var generos = listOf("Todos")
    private var autores = listOf("Todos")
    private var generoSeleccionado = "Todos"
    private var autorSeleccionado = "Todos"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kenneth_favoritos)

        findViewById<android.widget.ImageView>(R.id.btnAtras).setOnClickListener { finish() }

        cargarFiltros()
        cargarFavoritos()

        findViewById<EditText>(R.id.etBuscarFav).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = filtrar()
            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun cargarFiltros() {
        val db = AletheaBd(this).readableDatabase

        val cGen = db.rawQuery("SELECT DISTINCT COALESCE(categoria,'') FROM Libros ORDER BY categoria", null)
        val listaGen = mutableListOf("Todos")
        while (cGen.moveToNext()) { val g = cGen.getString(0); if (g.isNotEmpty()) listaGen.add(g) }
        cGen.close()
        generos = listaGen

        val cAut = db.rawQuery("SELECT DISTINCT COALESCE(autor_libro,'') FROM Libros ORDER BY autor_libro", null)
        val listaAut = mutableListOf("Todos")
        while (cAut.moveToNext()) { val a = cAut.getString(0); if (a.isNotEmpty()) listaAut.add(a) }
        cAut.close()
        db.close()
        autores = listaAut

        val spGenero = findViewById<Spinner>(R.id.spinnerGenero)
        spGenero.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, generos).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spGenero.setSelection(0)
        spGenero.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: android.view.View?, pos: Int, id: Long) {
                generoSeleccionado = generos[pos]; filtrar()
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }

        val spAutor = findViewById<Spinner>(R.id.spinnerAutor)
        spAutor.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, autores).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spAutor.setSelection(0)
        spAutor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: android.view.View?, pos: Int, id: Long) {
                autorSeleccionado = autores[pos]; filtrar()
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
    }

    data class FavLibro(
        val titulo: String, val autor: String, val categoria: String,
        val anio: String, val rutaImagen: String, val libroId: Int,
        val stock: Int, val disponibles: Int
    )

    private fun cargarFavoritos() {
        val usuarioId = SessionManager.getUsuarioId()
        if (usuarioId == -1) { renderFavoritos(emptyList()); return }

        val db = AletheaBd(this).readableDatabase
        val c = db.rawQuery(
            """SELECT l.nombre_libro, COALESCE(l.autor_libro,''), COALESCE(l.categoria,''),
                      COALESCE(l.ano_creado,''), COALESCE(l.ruta_imagen,''), l.id, l.stock,
                      MAX(l.stock - (SELECT COUNT(*) FROM Prestamos p
                          WHERE p.libro_id = l.id AND p.estado = 'Aceptada'), 0)
               FROM Favoritos f JOIN Libros l ON f.libro_id = l.id
               WHERE f.usuario_id = ? ORDER BY l.nombre_libro""",
            arrayOf(usuarioId.toString())
        )
        librosFav = mutableListOf()
        while (c.moveToNext()) {
            (librosFav as MutableList).add(
                FavLibro(
                    c.getString(0), c.getString(1), c.getString(2), c.getString(3),
                    c.getString(4), c.getInt(5), c.getInt(6), c.getInt(7)
                )
            )
        }
        c.close()
        db.close()
        filtrar()
    }

    private fun filtrar() {
        val query = findViewById<EditText>(R.id.etBuscarFav).text.toString().trim().lowercase()
        val filtrados = librosFav.filter { libro ->
            (generoSeleccionado == "Todos" || libro.categoria == generoSeleccionado) &&
            (autorSeleccionado == "Todos" || libro.autor == autorSeleccionado) &&
            (query.isEmpty() || libro.titulo.lowercase().contains(query) || libro.autor.lowercase().contains(query))
        }
        renderFavoritos(filtrados)
    }

    private fun renderFavoritos(items: List<FavLibro>) {
        val container = findViewById<LinearLayout>(R.id.listaFavoritos)
        container.removeAllViews()

        if (items.isEmpty()) {
            val empty = TextView(this)
            empty.text = "No tienes favoritos aún"
            empty.setTextColor(ContextCompat.getColor(this, R.color.kenneth_texto_vacio))
            empty.textSize = 14f
            empty.gravity = android.view.Gravity.CENTER
            empty.setPadding(0, 40, 0, 0)
            container.addView(empty)
            return
        }

        val inflater = LayoutInflater.from(this)
        items.forEach { libro ->
            val item = inflater.inflate(R.layout.item_favorito, container, false)

            item.findViewById<TextView>(R.id.tvTituloFav).text = libro.titulo
            item.findViewById<TextView>(R.id.tvAutorFav).text = libro.autor
            item.findViewById<TextView>(R.id.tvCategoriaFav).text = libro.categoria
            item.findViewById<TextView>(R.id.tvAnioFav).text = libro.anio
            val tvStock = item.findViewById<TextView>(R.id.tvStockFav)
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
                    item.findViewById<android.widget.ImageView>(R.id.ivPortadaFav).setImageBitmap(
                        BitmapFactory.decodeFile(archivo.absolutePath)
                    )
                }
            }

            item.setOnClickListener {
                val intent = Intent(this, DetalleLibroActivity::class.java)
                intent.putExtra("titulo", libro.titulo)
                startActivity(intent)
            }

            item.findViewById<android.view.View>(R.id.btnFavCorazon).setOnClickListener {
                val db = AletheaBd(this).writableDatabase
                db.execSQL("DELETE FROM Favoritos WHERE usuario_id = ? AND libro_id = ?",
                    arrayOf(SessionManager.getUsuarioId().toString(), libro.libroId.toString()))
                db.close()
                Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                cargarFavoritos()
            }

            container.addView(item)
        }
    }
}
