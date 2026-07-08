package com.example.alethea.MejiaRachel

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alethea.AletheaBd
import com.example.alethea.R

class PrestamosDevolucionesActivity : AppCompatActivity() {
    private val bd by lazy { AletheaBd(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rachel_prestamos_devoluciones)
        findViewById<android.widget.ImageView>(R.id.btnAtras).setOnClickListener { finish() }
        cargarDatos()
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    private fun cargarDatos() {
        cargarPendientes()
        cargarDevoluciones()
    }

    private fun cargarPendientes() {
        val db = bd.readableDatabase
        val cursor = db.rawQuery(
            """SELECT p.id, u.usuario, l.nombre_libro, p.fecha_prestamo, l.id, p.usuario_id
               FROM Prestamos p
               INNER JOIN Usuarios u ON p.usuario_id = u.id
               INNER JOIN Libros l ON p.libro_id = l.id
               WHERE p.estado = 'Pendiente'
               ORDER BY p.id DESC""", null
        )
        val lista = mutableListOf<PrestamoPendiente>()
        while (cursor.moveToNext()) {
            lista.add(
                PrestamoPendiente(
                    id = cursor.getInt(0),
                    usuarioNombre = cursor.getString(1),
                    libroNombre = cursor.getString(2),
                    fechaSolicitada = cursor.getString(3),
                    libroId = cursor.getInt(4),
                    usuarioId = cursor.getInt(5)
                )
            )
        }
        cursor.close()
        db.close()

        val rv = findViewById<RecyclerView>(R.id.rvPrestamosPendientes)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = PrestamoPendienteAdapter(lista.toList(),
            onAceptar = { p -> cambiarEstado(p, "Aceptada") },
            onRechazar = { p -> cambiarEstado(p, "Rechazado") }
        )
    }

    private fun cargarDevoluciones() {
        val db = bd.readableDatabase
        val cursor = db.rawQuery(
            """SELECT p.id, u.usuario, l.nombre_libro, p.fecha_prestamo, p.fecha_devolucion, p.estado
               FROM Prestamos p
               INNER JOIN Usuarios u ON p.usuario_id = u.id
               INNER JOIN Libros l ON p.libro_id = l.id
               WHERE p.estado IN ('Aceptada', 'Rechazado')
               ORDER BY p.id DESC""", null
        )
        val lista = mutableListOf<Devolucion>()
        while (cursor.moveToNext()) {
            lista.add(
                Devolucion(
                    prestamoId = cursor.getInt(0),
                    usuarioNombre = cursor.getString(1),
                    libroNombre = cursor.getString(2),
                    fechaPrestamo = cursor.getString(3),
                    fechaDevolucion = cursor.getString(4),
                    estado = cursor.getString(5)
                )
            )
        }
        cursor.close()
        db.close()

        val rv = findViewById<RecyclerView>(R.id.rvDevoluciones)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = DevolucionAdapter(lista.toList())
    }

    private fun cambiarEstado(p: PrestamoPendiente, nuevoEstado: String) {
        val accion = if (nuevoEstado == "Aceptada") "aceptar" else "rechazar"
        AlertDialog.Builder(this)
            .setTitle("$accion préstamo")
            .setMessage("¿${accion.replaceFirstChar { it.uppercaseChar() }} el préstamo de \"${p.libroNombre}\" de ${p.usuarioNombre}?")
            .setPositiveButton("Sí") { _, _ ->
                val db = bd.writableDatabase
                db.execSQL(
                    "UPDATE Prestamos SET estado = ? WHERE id = ?",
                    arrayOf(nuevoEstado, p.id.toString())
                )
                db.close()
                Toast.makeText(this, "Préstamo $nuevoEstado", Toast.LENGTH_SHORT).show()
                cargarDatos()
            }
            .setNegativeButton("No", null)
            .show()
    }
}