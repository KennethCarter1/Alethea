package com.example.alethea.EdgarRosario

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.alethea.AletheaBd
import com.example.alethea.R

class IniciarSesionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edgar_iniciar_sesion)

        findViewById<android.widget.LinearLayout>(R.id.btnIniciarSesion).setOnClickListener {
            val usuario = findViewById<EditText>(R.id.etUsuario).text.toString().trim()
            val contrasena = findViewById<EditText>(R.id.etContrasena).text.toString().trim()

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bd = AletheaBd(this)
            val db = bd.readableDatabase

            val cursor = db.rawQuery(
                "SELECT es_admin FROM Usuarios WHERE usuario = ? AND contrasena = ? LIMIT 1",
                arrayOf(usuario, contrasena)
            )

            if (cursor.moveToFirst()) {
                val esAdmin = cursor.getInt(0) == 1
                cursor.close()
                db.close()

                val destino =
                    if (esAdmin) BienvenidoAdminActivity::class.java
                    else BienvenidoUserActivity::class.java

                val intent =
                    Intent(
                        this,
                        destino
                    )
                startActivity(intent)
                finish()
            } else {
                cursor.close()
                db.close()
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<android.widget.TextView>(R.id.btnRegistrarse).setOnClickListener {
            val intent =
                Intent(
                    this,
                    RegistrarseActivity::class.java
                )
            startActivity(intent)
        }
    }
}
