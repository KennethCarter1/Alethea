package com.example.alethea.EdgarRosario

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.alethea.AletheaBd
import com.example.alethea.R

class RegistrarseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edgar_registrarse)

        findViewById<TextView>(R.id.btnIniciarSesionLink).setOnClickListener {
            startActivity(Intent(this, IniciarSesionActivity::class.java))
        }

        val etContrasena = findViewById<EditText>(R.id.etContrasena)
        togglePasswordVisibility(etContrasena, findViewById(R.id.ivOjoContrasena))

        findViewById<android.widget.LinearLayout>(R.id.btnRegistrarse).setOnClickListener {
            val usuario = findViewById<EditText>(R.id.etUsuario).text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = AletheaBd(this).writableDatabase
            try {
                db.execSQL(
                    "INSERT INTO Usuarios (nombre, apellido, correo, cedula, nacionalidad, usuario, contrasena, es_admin) VALUES (?, ?, ?, ?, ?, ?, ?, 0)",
                    arrayOf("", "", "", "", "", usuario, contrasena)
                )
                Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, IniciarSesionActivity::class.java))
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, "Error al registrar: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                db.close()
            }
        }
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