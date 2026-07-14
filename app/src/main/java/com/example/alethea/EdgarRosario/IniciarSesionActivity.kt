package com.example.alethea.EdgarRosario

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.alethea.AletheaBd
import com.example.alethea.R
import com.example.alethea.SessionManager

class IniciarSesionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edgar_iniciar_sesion)
        supportActionBar?.hide()

        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)

        findViewById<LinearLayout>(R.id.btnIniciarSesion).setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = AletheaBd(this).readableDatabase
            val cursor = db.rawQuery(
                "SELECT id, nombre, es_admin FROM Usuarios WHERE usuario = ? AND contrasena = ?",
                arrayOf(usuario, contrasena)
            )

            if (cursor.moveToFirst()) {
                val usuarioId = cursor.getInt(0)
                val nombre = cursor.getString(1)
                val esAdmin = cursor.getInt(2)
                cursor.close()
                db.close()

                SessionManager.guardarSesion(usuarioId, nombre, esAdmin == 1)

                if (esAdmin == 1) {
                    startActivity(Intent(this, BienvenidoAdminActivity::class.java))
                } else {
                    startActivity(Intent(this, BienvenidoUserActivity::class.java))
                }
                finish()
            } else {
                cursor.close()
                db.close()
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.btnRegistrarse).setOnClickListener {
            startActivity(Intent(this, RegistrarseActivity::class.java))
        }

        togglePasswordVisibility(etContrasena, findViewById(R.id.ivOjoContrasena))
    }

    private fun togglePasswordVisibility(et: EditText, iv: ImageView) {
        iv.setOnClickListener {
            if (et.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                et.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                iv.setImageResource(R.drawable.ic_visibilidad)
            } else {
                et.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                iv.setImageResource(R.drawable.ic_visibilidad_off)
            }
            et.text?.let { et.setSelection(it.length) }
        }
    }
}