package com.example.alethea.EdgarRosario

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alethea.R

class RegistrarseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edgar_registrarse)

        findViewById<android.widget.TextView>(R.id.btnIniciarSesionLink).setOnClickListener {
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
