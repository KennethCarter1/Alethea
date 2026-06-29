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
            val intent =
                Intent(
                    this,
                    DetalleLibroActivity::class.java
                )
            startActivity(intent)
        }
        findViewById<android.widget.LinearLayout>(R.id.catCard2).setOnClickListener {
            val intent =
                Intent(
                    this,
                    DetalleLibroActivity::class.java
                )
            startActivity(intent)
        }
        findViewById<android.widget.LinearLayout>(R.id.catCard3).setOnClickListener {
            val intent =
                Intent(
                    this,
                    DetalleLibroActivity::class.java
                )
            startActivity(intent)
        }
        findViewById<android.widget.LinearLayout>(R.id.catCard4).setOnClickListener {
            val intent =
                Intent(
                    this,
                    DetalleLibroActivity::class.java
                )
            startActivity(intent)
        }
    }
}
