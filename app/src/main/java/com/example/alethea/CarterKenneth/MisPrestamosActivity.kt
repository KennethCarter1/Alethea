package com.example.alethea.CarterKenneth

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

class MisPrestamosActivity : AppCompatActivity() {
    private val bd by lazy { AletheaBd(this) }
    private var filtroEstado = "Todos"
    private var textoBusqueda = ""

    data class PrestamoUser(
        val libroId: Int, val titulo: String, val autor: String,
        val estado: String, val fechaPrestamo: String, val fechaDevolucion: String,
        val fechaEntrega: String, val rutaImagen: String
    )

    private var todosLosPrestamos = listOf<PrestamoUser>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kenneth_mis_prestamos)
        findViewById<android.widget.ImageView>(R.id.btnAtras).setOnClickListener { finish() }
        cargarPrestamos()

        findViewById<EditText>(R.id.etBuscarPrestamo).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textoBusqueda = s.toString().trim().lowercase()
                aplicarFiltros()
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })

        val estados = listOf("Todos", "Aceptada", "Pendiente", "Atrasado", "Rechazado", "Devuelto")
        val spEstado = findViewById<Spinner>(R.id.spinnerEstado)
        spEstado.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spEstado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: android.view.View?, pos: Int, id: Long) {
                filtroEstado = estados[pos]; aplicarFiltros()
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
    }

    override fun onResume() {
        super.onResume()
        cargarPrestamos()
    }

    private fun cargarPrestamos() {
        val usuarioId = SessionManager.getUsuarioId()
        if (usuarioId == -1) return

        val db = bd.readableDatabase
        val cursor = db.rawQuery(
            """SELECT l.id, l.nombre_libro, l.autor_libro, p.estado, p.fecha_prestamo,
                      COALESCE(p.fecha_devolucion,''), COALESCE(p.fecha_entrega,''),
                      COALESCE(l.ruta_imagen,'')
               FROM Prestamos p
               INNER JOIN Libros l ON p.libro_id = l.id
               WHERE p.usuario_id = ?
               ORDER BY p.id DESC""", arrayOf(usuarioId.toString())
        )

        val lista = mutableListOf<PrestamoUser>()
        while (cursor.moveToNext()) {
            val estado = cursor.getString(3)
            val fechaDev = cursor.getString(5)
            val esAtrasado = estado == "Aceptada" && fechaDev.isNotEmpty() &&
                    fechaDev < java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())

            lista.add(
                PrestamoUser(
                    libroId = cursor.getInt(0),
                    titulo = cursor.getString(1),
                    autor = cursor.getString(2),
                    estado = if (esAtrasado) "Atrasado" else estado,
                    fechaPrestamo = cursor.getString(4),
                    fechaDevolucion = fechaDev,
                    fechaEntrega = cursor.getString(6),
                    rutaImagen = cursor.getString(7)
                )
            )
        }
        cursor.close()
        db.close()

        todosLosPrestamos = lista.toList()
        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        val filtrados = todosLosPrestamos.filter { p ->
            val coincideTexto = textoBusqueda.isEmpty() ||
                    p.titulo.lowercase().contains(textoBusqueda) ||
                    p.autor.lowercase().contains(textoBusqueda)
            val coincideEstado = filtroEstado == "Todos" || p.estado == filtroEstado
            coincideTexto && coincideEstado
        }
        renderPrestamos(filtrados)
    }

    private fun renderPrestamos(prestamos: List<PrestamoUser>) {
        val container = findViewById<LinearLayout>(R.id.listaPrestamos)
        container.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val usuarioId = SessionManager.getUsuarioId()

        for (p in prestamos) {
            val item = inflater.inflate(R.layout.item_prestamo_usuario, container, false)
            item.findViewById<TextView>(R.id.tvTituloLibro).text = p.titulo
            item.findViewById<TextView>(R.id.tvAutor).text = p.autor

            val tvEstado = item.findViewById<TextView>(R.id.tvEstado)
            tvEstado.text = when (p.estado) {
                "Atrasado" -> { tvEstado.setBackgroundResource(R.drawable.badge_atrasado); tvEstado.setTextColor(ContextCompat.getColor(this, R.color.kenneth_texto_estado)); "Atrasado" }
                "Aceptada" -> { tvEstado.setBackgroundResource(R.drawable.badge_aceptada); tvEstado.setTextColor(ContextCompat.getColor(this, R.color.kenneth_texto_estado)); "Aceptada" }
                "Devuelto" -> { tvEstado.setBackgroundResource(R.drawable.badge_aceptada); tvEstado.setTextColor(ContextCompat.getColor(this, R.color.kenneth_texto_estado)); getString(R.string.ken_devuelto) }
                "Rechazado" -> { tvEstado.setBackgroundResource(R.drawable.badge_rechazado); tvEstado.setTextColor(ContextCompat.getColor(this, R.color.kenneth_texto_estado)); "Rechazado" }
                else -> { tvEstado.setBackgroundResource(R.drawable.badge_pendiente); tvEstado.setTextColor(ContextCompat.getColor(this, R.color.kenneth_texto_estado)); "Pendiente" }
            }

            when (p.estado) {
                "Aceptada", "Atrasado" -> {
                    item.findViewById<TextView>(R.id.lblFecha1).text = getString(R.string.ken_fecha_prestamo)
                    item.findViewById<TextView>(R.id.tvFecha1).text = p.fechaPrestamo
                    item.findViewById<TextView>(R.id.lblFecha2).text = getString(R.string.ken_fecha_devolucion)
                    item.findViewById<TextView>(R.id.tvFecha2).text = p.fechaDevolucion
                }
                "Devuelto" -> {
                    item.findViewById<TextView>(R.id.lblFecha1).text = getString(R.string.ken_fecha_prestamo)
                    item.findViewById<TextView>(R.id.tvFecha1).text = p.fechaPrestamo
                    item.findViewById<TextView>(R.id.lblFecha2).text = getString(R.string.ken_fecha_entrega)
                    item.findViewById<TextView>(R.id.tvFecha2).text = p.fechaEntrega
                }
                "Rechazado" -> {
                    item.findViewById<TextView>(R.id.lblFecha1).text = getString(R.string.ken_fecha_solicitud)
                    item.findViewById<TextView>(R.id.tvFecha1).text = p.fechaPrestamo
                    item.findViewById<TextView>(R.id.lblFecha2).text = getString(R.string.ken_resultado)
                    item.findViewById<TextView>(R.id.tvFecha2).text = getString(R.string.ken_rechazo_admin)
                }
                else -> {
                    item.findViewById<TextView>(R.id.lblFecha1).text = getString(R.string.ken_fecha_solicitud)
                    item.findViewById<TextView>(R.id.tvFecha1).text = p.fechaPrestamo
                    item.findViewById<TextView>(R.id.lblFecha2).text = getString(R.string.ken_motivo)
                    item.findViewById<TextView>(R.id.tvFecha2).text = "En revisión"
                }
            }

            if (p.rutaImagen.isNotEmpty()) {
                val archivo = File(filesDir, "images/${p.rutaImagen}")
                if (archivo.exists()) {
                    item.findViewById<ImageView>(R.id.ivPortadaPrestamo).setImageBitmap(
                        BitmapFactory.decodeFile(archivo.absolutePath)
                    )
                }
            }

            val ivFav = item.findViewById<ImageView>(R.id.ivFavPrestamo)
            val db = bd.readableDatabase
            val c = db.rawQuery("SELECT id FROM Favoritos WHERE usuario_id = ? AND libro_id = ?", arrayOf(usuarioId.toString(), p.libroId.toString()))
            if (c.moveToFirst()) {
                ivFav.setColorFilter(ContextCompat.getColor(this, R.color.kenneth_favorito))
            }
            c.close()
            db.close()

            item.setOnClickListener {
                val intent = android.content.Intent(this, DetalleLibroActivity::class.java)
                intent.putExtra("titulo", p.titulo)
                startActivity(intent)
            }

            item.findViewById<android.view.View>(R.id.btnFavPrestamo).setOnClickListener {
                val dbw = bd.writableDatabase
                val cc = dbw.rawQuery("SELECT id FROM Favoritos WHERE usuario_id = ? AND libro_id = ?", arrayOf(usuarioId.toString(), p.libroId.toString()))
                val existe = cc.moveToFirst()
                cc.close()
                if (existe) {
                    dbw.execSQL("DELETE FROM Favoritos WHERE usuario_id = ? AND libro_id = ?", arrayOf(usuarioId.toString(), p.libroId.toString()))
                    ivFav.clearColorFilter()
                    Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                } else {
                    dbw.execSQL("INSERT INTO Favoritos(usuario_id, libro_id) VALUES(?, ?)", arrayOf(usuarioId.toString(), p.libroId.toString()))
                    ivFav.setColorFilter(ContextCompat.getColor(this, R.color.kenneth_favorito))
                    Toast.makeText(this, "Añadido a favoritos", Toast.LENGTH_SHORT).show()
                }
                dbw.close()
            }

            container.addView(item)
        }
    }
}
