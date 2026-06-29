package com.example.alethea.CarterKenneth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alethea.R

class CatalogoLibrosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kenneth_catalogo_libros)

        findViewById<android.widget.ImageView>(R.id.btnAtras).setOnClickListener { finish() }

        findViewById<android.widget.LinearLayout>(R.id.catCard1).setOnClickListener {
            startActivity(Intent(this, DetalleLibroActivity::class.java))
        }
        findViewById<android.widget.LinearLayout>(R.id.catCard2).setOnClickListener {
            startActivity(Intent(this, DetalleLibroActivity::class.java))
        }
        findViewById<android.widget.LinearLayout>(R.id.catCard3).setOnClickListener {
            startActivity(Intent(this, DetalleLibroActivity::class.java))
        }
        findViewById<android.widget.LinearLayout>(R.id.catCard4).setOnClickListener {
            startActivity(Intent(this, DetalleLibroActivity::class.java))
        }
    }
}
