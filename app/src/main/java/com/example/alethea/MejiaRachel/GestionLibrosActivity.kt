package com.example.alethea.MejiaRachel

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alethea.AletheaBd
import com.example.alethea.DelgadoJhezrrel.AgregarLibrosActivity
import com.example.alethea.R

class GestionLibrosActivity : AppCompatActivity() {
    private val bd by lazy { AletheaBd(this) }
    private var libros = mutableListOf<Libro>()
    private lateinit var rvLibros: RecyclerView
    private var adapter: LibroAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rachel_gestion_libros)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvLibros = findViewById(R.id.rvLibros)
        rvLibros.layoutManager = LinearLayoutManager(this)

        findViewById<android.widget.ImageView>(R.id.btnAtras).setOnClickListener { finish() }

        findViewById<android.widget.LinearLayout>(R.id.btnAgregarLibros).setOnClickListener {
            startActivity(Intent(this, AgregarLibrosActivity::class.java))
        }

        cargarLibros()

        findViewById<EditText>(R.id.etBuscarLibro).addTextChangedListener(
            object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    filtrar(s.toString().trim().lowercase())
                }
                override fun afterTextChanged(s: android.text.Editable?) = Unit
            }
        )
    }

    override fun onResume() {
        super.onResume()
        cargarLibros()
    }

    private fun cargarLibros() {
        libros.clear()
        val db = bd.readableDatabase
        val cursor = db.rawQuery(
            """SELECT l.id, l.nombre_libro, l.autor_libro, l.categoria,
                l.ano_creado, l.sinopsis, COALESCE(l.ruta_imagen,''), l.stock,
                MAX(l.stock - (SELECT COUNT(*) FROM Prestamos p
                    WHERE p.libro_id = l.id AND p.estado = 'Aceptada'), 0)
                FROM Libros l""".trimIndent(),
            null
        )
        while (cursor.moveToNext()) {
            libros.add(
                Libro(
                    id = cursor.getInt(0),
                    nombre = cursor.getString(1),
                    autor = cursor.getString(2),
                    categoria = cursor.getString(3),
                    anio = cursor.getString(4),
                    sinopsis = cursor.getString(5),
                    rutaImagen = cursor.getString(6),
                    stock = cursor.getInt(7),
                    disponibles = cursor.getInt(8)
                )
            )
        }
        cursor.close()
        db.close()

        adapter = LibroAdapter(
            listaLibros = libros.toList(),
            onEditar = { libro ->
                val intent = Intent(this, EditarLibroActivity::class.java)
                intent.putExtra("libro_id", libro.id)
                startActivity(intent)
            },
            onEliminar = { libro ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar libro")
                    .setMessage("¿Eliminar \"${libro.nombre}\"?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        val d = bd.writableDatabase
                        d.delete("Libros", "id = ?", arrayOf(libro.id.toString()))
                        d.close()
                        cargarLibros()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )
        rvLibros.adapter = adapter
        actualizarKpis()
    }

    private fun filtrar(texto: String) {
        val filtrados = if (texto.isEmpty()) libros
        else libros.filter {
            it.nombre.lowercase().contains(texto) || it.autor.lowercase().contains(texto)
        }
        adapter = LibroAdapter(
            listaLibros = filtrados,
            onEditar = { libro ->
                val intent = Intent(this, EditarLibroActivity::class.java)
                intent.putExtra("libro_id", libro.id)
                startActivity(intent)
            },
            onEliminar = { libro ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar libro")
                    .setMessage("¿Eliminar \"${libro.nombre}\"?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        val d = bd.writableDatabase
                        d.delete("Libros", "id = ?", arrayOf(libro.id.toString()))
                        d.close()
                        cargarLibros()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )
        rvLibros.adapter = adapter
    }

    private fun actualizarKpis() {
        val totalEjemplares = libros.sumOf { it.stock }
        findViewById<TextView>(R.id.tvTotalLibros).text = totalEjemplares.toString()
        val db = bd.readableDatabase
        val cursorP = db.rawQuery("SELECT COUNT(*) FROM Prestamos WHERE estado = 'Aceptada'", null)
        val prestados = if (cursorP.moveToFirst()) cursorP.getInt(0) else 0
        cursorP.close()
        db.close()
        val disponibles = (totalEjemplares - prestados).coerceAtLeast(0)
        findViewById<TextView>(R.id.tvDisponibles).text = disponibles.toString()
        findViewById<TextView>(R.id.tvPrestados).text = prestados.toString()
    }
}
