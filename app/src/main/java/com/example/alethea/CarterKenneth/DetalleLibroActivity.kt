package com.example.alethea.CarterKenneth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alethea.R

class DetalleLibroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kenneth_detalle_libro)

        findViewById<android.widget.ImageView>(R.id.btnAtras).setOnClickListener { finish() }

        findViewById<android.widget.LinearLayout>(R.id.btnSolicitarLibro).setOnClickListener {
            val intent =
                Intent(
                    this,
                    MisPrestamosActivity::class.java
                )
            startActivity(intent)
        }
    }
}
