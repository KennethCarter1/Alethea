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
import com.example.alethea.R

class GestionUsuariosActivity : AppCompatActivity() {
    private val usuarios = listOf(
        Usuario("Kenneth Carter", "correo@utp.ac.pa", "8-888-888", "Usuario", true),
        Usuario("Edgar Rosario", "edgar@utp.ac.pa", "8-777-777", "Admin", true),
        Usuario("Rachel Mejia", "rachel@utp.ac.pa", "8-666-666", "Usuario", true),
        Usuario("Jhezrrel Delgado", "jhezrrel@utp.ac.pa", "8-555-555", "Admin", true),
        Usuario("Maria Lopez", "maria@utp.ac.pa", "8-444-444", "Usuario", false),
        Usuario("Carlos Ruiz", "carlos@utp.ac.pa", "8-333-333", "Usuario", true)
    )

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
        findViewById<TextView>(R.id.tvTotalUsuarios).text = usuarios.size.toString().padStart(2, '0')
        findViewById<TextView>(R.id.tvTotalAceptados).text = usuarios.count { it.aceptado }.toString().padStart(2, '0')

        renderUsuarios(usuarios)
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
