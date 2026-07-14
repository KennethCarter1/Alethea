package com.example.alethea.DelgadoJhezrrel

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.alethea.AletheaBd
import com.example.alethea.R

class GestionUsuariosActivity : AppCompatActivity() {
    private val bd by lazy { AletheaBd(this) }
    private var usuarios = listOf<Usuario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jhezrrel_gestion_usuarios)
        supportActionBar?.hide()

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
            val nombre = cursor.getString(1) ?: ""
            val apellido = cursor.getString(2) ?: ""
            val correo = cursor.getString(3) ?: ""
            val cedula = cursor.getString(4) ?: ""
            val esAdmin = cursor.getInt(5) == 1

            val cursorPrestamos = db.rawQuery(
                "SELECT COUNT(*) FROM Prestamos WHERE usuario_id = ?", arrayOf(id.toString())
            )
            val tienePrestamos = if (cursorPrestamos.moveToFirst()) cursorPrestamos.getInt(0) > 0 else false
            cursorPrestamos.close()

            (usuarios as MutableList).add(
                Usuario(
                    nombre = "$nombre $apellido",
                    correo = correo ?: "",
                    cedula = cedula ?: "",
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
        val container = findViewById<LinearLayout>(R.id.listaUsuarios)
        container.removeAllViews()
        val inflater = LayoutInflater.from(this)

        items.forEach { usuario ->
            val item = inflater.inflate(R.layout.item_usuario_gestion, container, false)
            item.findViewById<TextView>(R.id.tvNombre).text = usuario.nombre
            item.findViewById<TextView>(R.id.tvCedula).text = usuario.cedula
            item.findViewById<TextView>(R.id.tvCorreo).text = usuario.correo
            val tvRol = item.findViewById<TextView>(R.id.tvRol)
            if (usuario.rol == "Admin") {
                tvRol.text = "Admin"
                tvRol.setBackgroundResource(R.drawable.badge_admin)
                tvRol.setTextColor(android.graphics.Color.BLACK)
            } else {
                tvRol.text = "Usuario"
                tvRol.setBackgroundResource(R.drawable.badge_aceptada)
                tvRol.setTextColor(android.graphics.Color.WHITE)
            }
            container.addView(item)
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
