package com.example.alethea.EdgarRosario

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.alethea.AletheaBd
import com.example.alethea.R

class InicioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edgar_inicio)
        supportActionBar?.hide()

        val etBuscar = findViewById<EditText>(R.id.etBuscar)
        val contenedorResultados = findViewById<LinearLayout>(R.id.contenedorResultados)

        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                contenedorResultados.removeAllViews()

                if (query.length >= 2) {
                    val db = AletheaBd(applicationContext).readableDatabase
                    val cursor = db.rawQuery(
                        "SELECT nombre_libro, autor_libro, categoria FROM Libros WHERE nombre_libro LIKE ? OR autor_libro LIKE ?",
                        arrayOf("%$query%", "%$query%")
                    )

                    if (cursor.moveToFirst()) {
                        do {
                            val nombre = cursor.getString(0)
                            val autor = cursor.getString(1)
                            val categoria = cursor.getString(2)

                            val tv = TextView(applicationContext).apply {
                                text = "📖 $nombre\n$autor · $categoria"
                                textSize = 14f
                                setPadding(24, 16, 24, 16)
                                setTextColor(resources.getColor(R.color.edgar_texto_principal, null))
                                setBackgroundResource(R.drawable.edgar_campo_borde)
                                val params = android.widget.LinearLayout.LayoutParams(
                                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                params.setMargins(0, 0, 0, 8)
                                layoutParams = params
                            }
                            contenedorResultados.addView(tv)
                        } while (cursor.moveToNext())
                    } else {
                        val tv = TextView(applicationContext).apply {
                            text = "No se encontraron libros"
                            textSize = 14f
                            setPadding(16, 12, 16, 12)
                            setTextColor(resources.getColor(R.color.edgar_texto_secundario, null))
                        }
                        contenedorResultados.addView(tv)
                    }
                    cursor.close()
                    db.close()
                }
            }
        })

        findViewById<LinearLayout>(R.id.btnIniciarSesion).setOnClickListener {
            startActivity(Intent(this, IniciarSesionActivity::class.java))
        }
    }
}
