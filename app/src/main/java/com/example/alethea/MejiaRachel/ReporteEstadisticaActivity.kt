package com.example.alethea.MejiaRachel

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.alethea.AletheaBd
import com.example.alethea.R

class ReporteEstadisticaActivity : AppCompatActivity() {
    private val bd by lazy { AletheaBd(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rachel_reporte_estadistica)
        findViewById<android.widget.ImageView>(R.id.btnAtras).setOnClickListener { finish() }
        cargarKpis()
        cargarCategorias()
        findViewById<android.view.View>(R.id.btnExportarDatos).setOnClickListener {
            Toast.makeText(this, "Exportación no disponible aún", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarKpis() {
        val db = bd.readableDatabase
        val cursorU = db.rawQuery("SELECT COUNT(*) FROM Usuarios", null)
        val totalU = if (cursorU.moveToFirst()) cursorU.getInt(0) else 0
        cursorU.close()

        val cursorL = db.rawQuery("SELECT COUNT(*) FROM Libros", null)
        val totalL = if (cursorL.moveToFirst()) cursorL.getInt(0) else 0
        cursorL.close()

        val cursorA = db.rawQuery("SELECT COUNT(DISTINCT autor_libro) FROM Libros", null)
        val totalA = if (cursorA.moveToFirst()) cursorA.getInt(0) else 0
        cursorA.close()

        val cursorP = db.rawQuery("SELECT COUNT(*) FROM Prestamos", null)
        val totalP = if (cursorP.moveToFirst()) cursorP.getInt(0) else 0
        cursorP.close()
        db.close()

        findViewById<TextView>(R.id.tvTotalUsuarios).text = totalU.toString()
        findViewById<TextView>(R.id.tvTotalLibros).text = totalL.toString()
        findViewById<TextView>(R.id.tvTotalAutores).text = totalA.toString()
        findViewById<TextView>(R.id.tvTotalPrestamos).text = totalP.toString()
    }

    private fun cargarCategorias() {
        val db = bd.readableDatabase
        val cursor = db.rawQuery("SELECT categoria, COUNT(*) FROM Libros GROUP BY categoria", null)
        val container = findViewById<LinearLayout>(R.id.contenedorCategorias)

        while (cursor.moveToNext()) {
            val nombre = cursor.getString(0)
            val cantidad = cursor.getInt(1)

            val row = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 10 }
                orientation = LinearLayout.HORIZONTAL
            }

            val tvNombre = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = nombre
                setTextColor(resources.getColor(R.color.gris_medio, theme))
                textSize = 14f
            }

            val tvCant = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                text = cantidad.toString()
                setTextColor(resources.getColor(R.color.negro_suave, theme))
                textSize = 14f
            }

            row.addView(tvNombre)
            row.addView(tvCant)
            container.addView(row)
        }
        cursor.close()
        db.close()
    }
}