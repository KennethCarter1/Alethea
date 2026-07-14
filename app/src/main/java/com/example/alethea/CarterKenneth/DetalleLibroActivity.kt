package com.example.alethea.CarterKenneth

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.alethea.AletheaBd
import com.example.alethea.R
import com.example.alethea.SessionManager
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetalleLibroActivity : AppCompatActivity() {
    private var libroId = -1
    private var prestamoId = -1
    private var estadoActual = ""
    private var stockTotal = 0
    private var stockDisponible = 0
    private var tituloActual = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kenneth_detalle_libro)

        findViewById<ImageView>(R.id.btnAtras).setOnClickListener { finish() }

        val titulo = intent.getStringExtra("titulo")
        if (titulo != null) {
            tituloActual = titulo
            cargarDatos(titulo)
        }

        findViewById<LinearLayout>(R.id.btnSolicitarLibro).setOnClickListener {
            val usuarioId = SessionManager.getUsuarioId()
            if (usuarioId == -1) {
                Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (stockDisponible <= 0) {
                Toast.makeText(this, getString(R.string.ken_sin_stock), Toast.LENGTH_SHORT).show()
            } else if (prestamoId == -1) {
                crearSolicitud(usuarioId)
            } else {
                Toast.makeText(this, "Este libro ya está $estadoActual", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<android.view.View>(R.id.btnFavorito).setOnClickListener {
            toggleFavorito()
        }
    }

    private fun cargarDatos(titulo: String) {
        val db = AletheaBd(this).readableDatabase
        val cursor = db.rawQuery(
            """SELECT l.id, COALESCE(l.ruta_imagen,''), l.stock,
                      MAX(l.stock - (SELECT COUNT(*) FROM Prestamos p
                          WHERE p.libro_id = l.id AND p.estado = 'Aceptada'), 0)
               FROM Libros l WHERE l.nombre_libro = ?""".trimIndent(),
            arrayOf(titulo)
        )
        if (cursor.moveToFirst()) {
            libroId = cursor.getInt(0)
            val ruta = cursor.getString(1)
            stockTotal = cursor.getInt(2)
            stockDisponible = cursor.getInt(3)
            if (ruta.isNotEmpty()) {
                val archivo = File(filesDir, "images/$ruta")
                if (archivo.exists()) {
                    findViewById<ImageView>(R.id.ivPortadaDetalle).setImageBitmap(
                        BitmapFactory.decodeFile(archivo.absolutePath)
                    )
                }
            }
        }
        cursor.close()
        db.close()

        mostrarDisponibilidad()
        cargarEstado()

        val usuarioId = SessionManager.getUsuarioId()
        if (usuarioId != -1 && libroId != -1) {
            val db2 = AletheaBd(this).readableDatabase
            val c = db2.rawQuery(
                "SELECT id FROM Favoritos WHERE usuario_id = ? AND libro_id = ?",
                arrayOf(usuarioId.toString(), libroId.toString())
            )
            if (c.moveToFirst()) {
                findViewById<ImageView>(R.id.ivFavDetalle).setColorFilter(
                    ContextCompat.getColor(this, R.color.kenneth_favorito)
                )
            }
            c.close()
            db2.close()
        }
    }

    private fun cargarEstado() {
        val usuarioId = SessionManager.getUsuarioId()
        if (libroId == -1) return
        if (usuarioId == -1) {
            prepararBotonSolicitud()
            return
        }

        val db = AletheaBd(this).readableDatabase
        val c = db.rawQuery(
            "SELECT id, estado, fecha_devolucion FROM Prestamos WHERE usuario_id = ? AND libro_id = ? ORDER BY id DESC LIMIT 1",
            arrayOf(usuarioId.toString(), libroId.toString())
        )
        val btn = findViewById<LinearLayout>(R.id.btnSolicitarLibro)
        val btnText = findViewById<TextView>(R.id.tvBtnSolicitar)

        if (c.moveToFirst() && c.getString(1) in listOf("Pendiente", "Aceptada")) {
            prestamoId = c.getInt(0)
            estadoActual = c.getString(1)

            if (estadoActual == "Aceptada" && !c.isNull(2)) {
                val fechaDev = c.getString(2)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                try {
                    val devDate = sdf.parse(fechaDev)
                    if (devDate != null && devDate.before(Date())) {
                        estadoActual = "Atrasado"
                    }
                } catch (_: Exception) {}
            }

            when (estadoActual) {
                "Aceptada" -> {
                    btnText.text = "Aceptada"
                    btn.setBackgroundResource(R.drawable.kenneth_boton_estado_aceptado)
                }
                "Pendiente" -> {
                    btnText.text = "Pendiente"
                    btn.setBackgroundResource(R.drawable.kenneth_boton_estado_pendiente)
                }
                "Atrasado" -> {
                    btnText.text = "Atrasado"
                    btn.setBackgroundResource(R.drawable.kenneth_boton_estado_atrasado)
                }
            }
        } else {
            prepararBotonSolicitud()
        }
        c.close()
        db.close()
    }

    private fun mostrarDisponibilidad() {
        val tvStock = findViewById<TextView>(R.id.tvStockDetalle)
        if (stockDisponible == 0) {
            tvStock.text = getString(R.string.ken_sin_stock)
            tvStock.setBackgroundResource(R.drawable.kenneth_badge_sin_stock)
            tvStock.setTextColor(ContextCompat.getColor(this, R.color.kenneth_sin_stock_texto))
            tvStock.setPadding(12, 5, 12, 5)
        } else {
            tvStock.text = getString(R.string.ken_disponibles_formato, stockDisponible, stockTotal)
            tvStock.background = null
            tvStock.setTextColor(ContextCompat.getColor(this, R.color.kenneth_texto_principal))
        }
    }

    private fun prepararBotonSolicitud() {
        prestamoId = -1
        estadoActual = ""
        val btn = findViewById<LinearLayout>(R.id.btnSolicitarLibro)
        val btnText = findViewById<TextView>(R.id.tvBtnSolicitar)
        if (stockDisponible == 0) {
            btnText.text = getString(R.string.ken_sin_stock)
            btnText.setTextColor(ContextCompat.getColor(this, R.color.kenneth_sin_stock_texto))
            btn.setBackgroundResource(R.drawable.kenneth_boton_sin_stock)
            btn.isEnabled = false
        } else {
            btnText.text = getString(R.string.ken_solicitar_libro)
            btnText.setTextColor(ContextCompat.getColor(this, R.color.kenneth_texto_boton))
            btn.setBackgroundResource(R.drawable.kenneth_boton_principal)
            btn.isEnabled = true
        }
    }

    private fun crearSolicitud(usuarioId: Int) {
        val db = AletheaBd(this).writableDatabase
        var creada = false
        db.beginTransaction()
        try {
            val cursor = db.rawQuery(
                """SELECT MAX(l.stock - (SELECT COUNT(*) FROM Prestamos p
                    WHERE p.libro_id = l.id AND p.estado = 'Aceptada'), 0)
                    FROM Libros l WHERE l.id = ?""".trimIndent(),
                arrayOf(libroId.toString())
            )
            val disponibles = if (cursor.moveToFirst()) cursor.getInt(0) else 0
            cursor.close()

            if (disponibles > 0) {
                val hoy = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                db.execSQL(
                    "INSERT INTO Prestamos(usuario_id, libro_id, fecha_prestamo, estado) VALUES(?, ?, ?, 'Pendiente')",
                    arrayOf(usuarioId.toString(), libroId.toString(), hoy)
                )
                creada = true
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }

        if (creada) {
            Toast.makeText(this, "Solicitud enviada, espera la aprobación del admin", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.ken_sin_stock), Toast.LENGTH_SHORT).show()
        }
        cargarDatos(tituloActual)
    }

    private fun toggleFavorito() {
        val usuarioId = SessionManager.getUsuarioId()
        if (usuarioId == -1 || libroId == -1) {
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show()
            return
        }
        val db = AletheaBd(this).writableDatabase
        val cursor = db.rawQuery(
            "SELECT id FROM Favoritos WHERE usuario_id = ? AND libro_id = ?",
            arrayOf(usuarioId.toString(), libroId.toString())
        )
        val existe = cursor.moveToFirst()
        cursor.close()

        val heart = findViewById<ImageView>(R.id.ivFavDetalle)
        if (existe) {
            db.execSQL("DELETE FROM Favoritos WHERE usuario_id = ? AND libro_id = ?", arrayOf(usuarioId.toString(), libroId.toString()))
            heart.clearColorFilter()
            Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
        } else {
            db.execSQL("INSERT INTO Favoritos(usuario_id, libro_id) VALUES(?, ?)", arrayOf(usuarioId.toString(), libroId.toString()))
            heart.setColorFilter(ContextCompat.getColor(this, R.color.kenneth_favorito))
            Toast.makeText(this, "Añadido a favoritos", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }
}
