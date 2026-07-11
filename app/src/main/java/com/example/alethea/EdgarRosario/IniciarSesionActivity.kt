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
                "SELECT es_admin FROM Usuarios WHERE usuario = ? AND contrasena = ?",
                arrayOf(usuario, contrasena)
            )

            if (cursor.moveToFirst()) {
                val esAdmin = cursor.getInt(0)
                cursor.close()
                db.close()

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
    }
}