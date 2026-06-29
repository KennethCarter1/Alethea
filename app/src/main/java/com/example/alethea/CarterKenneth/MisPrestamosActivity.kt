package com.example.alethea.CarterKenneth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alethea.R

class MisPrestamosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kenneth_mis_prestamos)
        findViewById<android.widget.ImageView>(R.id.btnAtras).setOnClickListener { finish() }
    }
}
