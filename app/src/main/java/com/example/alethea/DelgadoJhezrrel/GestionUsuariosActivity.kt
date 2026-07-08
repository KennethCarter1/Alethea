package com.example.alethea.DelgadoJhezrrel

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.alethea.AletheaBd
import com.example.alethea.R

class GestionUsuariosActivity : AppCompatActivity() {
    private val bd by lazy { AletheaBd(this) }
    private var usuarios = listOf<Usuario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_jhezrrel_gestion_usuarios)
        supportActionBar?.hide()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<android.widget.ImageView>(R.id.btnVolver).setOnClickListener { finish() }
        cargarUsuarios()
        findViewById<EditText>(R.id.etBuscarUsuario).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filtro = s.toString().trim().lowercase()
                renderUsuarios(usuarios.filter {
                    it.nombre.lowercase().contains(filtro) || it.correo.lowercase().contains(filtro)
                })
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun cargarUsuarios() {
        usuarios = mutableListOf()
        val db = bd.readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, nombre, apellido, correo, cedula, es_admin FROM Usuarios ORDER BY id", null
        )
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val nombre = cursor.getString(1)
            val apellido = cursor.getString(2)
            val correo = cursor.getString(3)
            val cedula = cursor.getString(4)
            val esAdmin = cursor.getInt(5) == 1

            val cursorPrestamos = db.rawQuery(
                "SELECT COUNT(*) FROM Prestamos WHERE usuario_id = ?", arrayOf(id.toString())
            )
            val tienePrestamos = if (cursorPrestamos.moveToFirst()) cursorPrestamos.getInt(0) > 0 else false
            cursorPrestamos.close()

            (usuarios as MutableList).add(
                Usuario(
                    nombre = "$nombre $apellido",
                    correo = correo,
                    cedula = cedula,
                    rol = if (esAdmin) "Admin" else "Usuario",
                    aceptado = tienePrestamos
                )
            )
        }
        cursor.close()
        db.close()

        findViewById<TextView>(R.id.tvTotalUsuarios).text = usuarios.size.toString().padStart(2, '0')
        findViewById<TextView>(R.id.tvTotalAceptados).text = usuarios.count { it.aceptado }.toString().padStart(2, '0')
        renderUsuarios(usuarios)
    }

    private fun renderUsuarios(items: List<Usuario>) {
        val lista = findViewById<LinearLayout>(R.id.listaUsuarios)
        lista.removeAllViews()
        items.forEach { usuario ->
            val fila = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }

            fila.addView(LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                addView(TextView(context).apply {
                    text = usuario.nombre
                    textSize = 22f
                    setTextColor(getColor(R.color.negro_suave))
                })
                addView(TextView(context).apply {
                    text = usuario.cedula
                    textSize = 16f
                    setTextColor(getColor(R.color.gris_medio))
                })
            })

            fila.addView(TextView(this).apply {
                text = usuario.correo
                textSize = 14f
                setTextColor(getColor(R.color.gris_medio))
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            })

            fila.addView(TextView(this).apply {
                text = usuario.rol
                textSize = 16f
                setTextColor(getColor(R.color.gris_medio))
                gravity = Gravity.END
                layoutParams = LinearLayout.LayoutParams(90, LinearLayout.LayoutParams.WRAP_CONTENT)
            })

            lista.addView(fila)
            lista.addView(View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
                ).apply {
                    setMargins(8, 12, 8, 14)
                }
                setBackgroundColor(getColor(R.color.gris_medio))
            })
        }
    }

    data class Usuario(
        val nombre: String,
        val correo: String,
        val cedula: String,
        val rol: String,
        val aceptado: Boolean
    )
}