package com.example.alethea.EdgarRosario

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alethea.R

class IniciarSesionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edgar_iniciar_sesion)

        findViewById<android.widget.LinearLayout>(R.id.btnIniciarSesion).setOnClickListener {
            startActivity(Intent(this, BienvenidoUserActivity::class.java))
        }

        findViewById<android.widget.TextView>(R.id.btnRegistrarse).setOnClickListener {
            startActivity(Intent(this, RegistrarseActivity::class.java))
        }
    }
}
