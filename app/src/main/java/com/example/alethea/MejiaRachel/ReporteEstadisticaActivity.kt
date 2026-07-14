package com.example.alethea.MejiaRachel

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.alethea.AletheaBd
import com.example.alethea.AletheaPDF
import com.example.alethea.R

class ReporteEstadisticaActivity : AppCompatActivity() {
    private val bd by lazy { AletheaBd(this) }

    private val iconosCategoria = intArrayOf(
        R.drawable.ic_libros_pila,
        R.drawable.ic_libro,
        R.drawable.ic_catalogo,
        R.drawable.ic_marcador,
        R.drawable.ic_calendario,
        R.drawable.ic_prestamo,
        R.drawable.ic_favoritos,
        R.drawable.ic_ajustes
    )

    // Abre el selector de Android y recibe la ubicación donde se guardará el PDF.
    private val exportarLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf")
    ) { destino ->
        if (destino != null) exportarPdf(destino)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rachel_reporte_estadistica)
        findViewById<android.widget.ImageView>(R.id.btnAtras).setOnClickListener { finish() }
        cargarKpis()
        cargarCategorias()
        findViewById<android.view.View>(R.id.btnExportarDatos).setOnClickListener {
            exportarLauncher.launch("Reporte_Alethea.pdf")
        }
    }

    private fun cargarKpis() {
        val db = bd.readableDatabase
        val cursorU = db.rawQuery("SELECT COUNT(*) FROM Usuarios", null)
        val totalU = if (cursorU.moveToFirst()) cursorU.getInt(0) else 0
        cursorU.close()

        val cursorL = db.rawQuery("SELECT COALESCE(SUM(stock), 0) FROM Libros", null)
        val totalL = if (cursorL.moveToFirst()) cursorL.getInt(0) else 0
        cursorL.close()

        val cursorA = db.rawQuery("SELECT COUNT(DISTINCT autor_libro) FROM Libros", null)
        val totalA = if (cursorA.moveToFirst()) cursorA.getInt(0) else 0
        cursorA.close()

        val cursorP = db.rawQuery("SELECT COUNT(*) FROM Prestamos", null)
        val totalP = if (cursorP.moveToFirst()) cursorP.getInt(0) else 0
        cursorP.close()

        val cursorPend = db.rawQuery("SELECT COUNT(*) FROM Prestamos WHERE estado = 'Pendiente'", null)
        val pendientes = if (cursorPend.moveToFirst()) cursorPend.getInt(0) else 0
        cursorPend.close()

        val cursorAcep = db.rawQuery("SELECT COUNT(*) FROM Prestamos WHERE estado = 'Aceptada'", null)
        val aceptados = if (cursorAcep.moveToFirst()) cursorAcep.getInt(0) else 0
        cursorAcep.close()

        db.close()

        findViewById<TextView>(R.id.tvTotalUsuarios).text = totalU.toString()
        findViewById<TextView>(R.id.tvTotalLibros).text = totalL.toString()
        findViewById<TextView>(R.id.tvTotalAutores).text = totalA.toString()
        findViewById<TextView>(R.id.tvTotalPrestamos).text = totalP.toString()

        findViewById<TextView>(R.id.tvPendientes).text = pendientes.toString()
        findViewById<TextView>(R.id.tvAceptados).text = aceptados.toString()
    }

    private fun cargarCategorias() {
        val db = bd.readableDatabase
        val cursor = db.rawQuery("SELECT categoria, COALESCE(SUM(stock), 0) FROM Libros GROUP BY categoria", null)
        val container = findViewById<LinearLayout>(R.id.contenedorCategorias)
        val inflater = LayoutInflater.from(this)

        var idx = 0
        while (cursor.moveToNext()) {
            val nombre = cursor.getString(0)
            val cantidad = cursor.getInt(1)

            val item = inflater.inflate(R.layout.item_categoria_reporte, container, false)
            item.findViewById<TextView>(R.id.tvCategoria).text = nombre
            item.findViewById<TextView>(R.id.tvCantidad).text = cantidad.toString()

            val iv = item.findViewById<ImageView>(R.id.ivIcono)
            iv.setImageDrawable(ResourcesCompat.getDrawable(resources, iconosCategoria[idx % iconosCategoria.size], theme))

            container.addView(item)
            idx++
        }
        cursor.close()
        db.close()
    }

    // Genera el reporte y muestra el resultado real de la exportación.
    private fun exportarPdf(destino: Uri) {
        val exportado = AletheaPDF.generarReporte(this, destino)
        if (exportado) {
            Toast.makeText(this, "PDF exportado correctamente", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No se pudo exportar el PDF", Toast.LENGTH_SHORT).show()
        }
    }
}
