package com.example.alethea.EdgarRosario

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alethea.R

class IniciarSesionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edgar_iniciar_sesion)
        supportActionBar?.hide()
    }
}