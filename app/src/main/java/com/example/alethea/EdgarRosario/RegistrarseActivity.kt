package com.example.alethea.EdgarRosario

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.alethea.AletheaBd
import com.example.alethea.R

class RegistrarseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edgar_registrarse)

        val bd = AletheaBd(this)

        findViewById<TextView>(R.id.btnIniciarSesionLink).setOnClickListener {
            val intent =
                Intent(
                    this,
                    IniciarSesionActivity::class.java
                )
            startActivity(intent)
            finish()
        }

        findViewById<LinearLayout>(R.id.btnRegistrarse).setOnClickListener {
            val usuario = findViewById<EditText>(R.id.etUsuario).text.toString().trim()
            val contrasena = findViewById<EditText>(R.id.etContrasena).text.toString().trim()

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = bd.writableDatabase
            val valores = android.content.ContentValues().apply {
                put("usuario", usuario)
                put("contrasena", contrasena)
            }
            val resultado = db.insert("Usuarios", null, valores)
            db.close()

            if (resultado == -1L) {
                Toast.makeText(this, "Error al registrarse", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Registrado correctamente", Toast.LENGTH_SHORT).show()
                val intent =
                    Intent(
                        this,
                        IniciarSesionActivity::class.java
                    )
                startActivity(intent)
                finish()
            }
        }
    }
}
