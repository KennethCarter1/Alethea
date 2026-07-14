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
            """SELECT p.id, u.nombre || ' ' || u.apellido, l.nombre_libro, p.fecha_prestamo, l.id, p.usuario_id,
                      MAX(l.stock - (SELECT COUNT(*) FROM Prestamos activos
                          WHERE activos.libro_id = l.id AND activos.estado = 'Aceptada'), 0)
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
                    usuarioId = cursor.getInt(5),
                    disponibles = cursor.getInt(6)
                )
            )
        }
        val totalPendientes = cursor.count
        cursor.close()
        db.close()

        findViewById<android.widget.TextView>(R.id.tvKpiPendientes).text = totalPendientes.toString()

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
            """SELECT p.id, u.nombre || ' ' || u.apellido, l.nombre_libro, p.fecha_prestamo,
                      p.fecha_devolucion, p.estado, p.fecha_entrega
               FROM Prestamos p
               INNER JOIN Usuarios u ON p.usuario_id = u.id
               INNER JOIN Libros l ON p.libro_id = l.id
               WHERE p.estado IN ('Aceptada', 'Rechazado', 'Devuelto')
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
                    estado = cursor.getString(5),
                    fechaEntrega = cursor.getString(6)
                )
            )
        }
        val totalHistorial = cursor.count
        cursor.close()
        db.close()

        findViewById<android.widget.TextView>(R.id.tvKpiHistorial).text = totalHistorial.toString()

        val rv = findViewById<RecyclerView>(R.id.rvDevoluciones)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = DevolucionAdapter(lista.toList()) { devolucion ->
            marcarDevuelto(devolucion)
        }
    }

    private fun cambiarEstado(p: PrestamoPendiente, nuevoEstado: String) {
        val accion = if (nuevoEstado == "Aceptada") "aceptar" else "rechazar"
        AlertDialog.Builder(this)
            .setTitle("$accion préstamo")
            .setMessage("¿${accion.replaceFirstChar { it.uppercaseChar() }} el préstamo de \"${p.libroNombre}\" de ${p.usuarioNombre}?")
            .setPositiveButton("Sí") { _, _ ->
                if (nuevoEstado == "Aceptada") {
                    aceptarPrestamo(p)
                } else {
                    val db = bd.writableDatabase
                    db.execSQL(
                        "UPDATE Prestamos SET estado = ? WHERE id = ?",
                        arrayOf(nuevoEstado, p.id.toString())
                    )
                    db.close()
                    Toast.makeText(this, "Préstamo rechazado", Toast.LENGTH_SHORT).show()
                    cargarDatos()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun aceptarPrestamo(prestamo: PrestamoPendiente) {
        val db = bd.writableDatabase
        var aceptado = false
        db.beginTransaction()
        try {
            val cursor = db.rawQuery(
                """SELECT MAX(l.stock - (SELECT COUNT(*) FROM Prestamos activos
                    WHERE activos.libro_id = l.id AND activos.estado = 'Aceptada'), 0)
                    FROM Libros l WHERE l.id = ?""".trimIndent(),
                arrayOf(prestamo.libroId.toString())
            )
            val disponibles = if (cursor.moveToFirst()) cursor.getInt(0) else 0
            cursor.close()

            if (disponibles > 0) {
                val sentencia = db.compileStatement(
                    """UPDATE Prestamos
                       SET estado = 'Aceptada', fecha_devolucion = date('now','localtime','+7 days'), fecha_entrega = NULL
                       WHERE id = ? AND estado = 'Pendiente'""".trimIndent(),
                )
                sentencia.bindLong(1, prestamo.id.toLong())
                aceptado = sentencia.executeUpdateDelete() > 0
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }

        if (aceptado) {
            Toast.makeText(this, "Préstamo aceptado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No hay stock disponible", Toast.LENGTH_LONG).show()
        }
        cargarDatos()
    }

    private fun marcarDevuelto(devolucion: Devolucion) {
        AlertDialog.Builder(this)
            .setTitle("Registrar devolución")
            .setMessage("¿Marcar \"${devolucion.libroNombre}\" como devuelto?")
            .setPositiveButton("Sí") { _, _ ->
                val db = bd.writableDatabase
                db.execSQL(
                    """UPDATE Prestamos
                       SET estado = 'Devuelto', fecha_entrega = date('now','localtime')
                       WHERE id = ? AND estado = 'Aceptada'""".trimIndent(),
                    arrayOf(devolucion.prestamoId.toString())
                )
                db.close()
                Toast.makeText(this, "Devolución registrada", Toast.LENGTH_SHORT).show()
                cargarDatos()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
