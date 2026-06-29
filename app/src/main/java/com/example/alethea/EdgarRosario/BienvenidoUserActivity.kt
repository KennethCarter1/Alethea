package com.example.alethea.EdgarRosario

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alethea.CarterKenneth.CatalogoLibrosActivity
import com.example.alethea.MusicManager
import com.example.alethea.CarterKenneth.FavoritosActivity
import com.example.alethea.CarterKenneth.MisPrestamosActivity
import com.example.alethea.CarterKenneth.PerfilUsuarioActivity
import com.example.alethea.DelgadoJhezrrel.AjustesActivity
import com.example.alethea.R

class BienvenidoUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edgar_bienvenido_user)

        MusicManager.iniciar(this)

        findViewById<androidx.cardview.widget.CardView>(R.id.btnCatalogo).setOnClickListener {
            val intent =
                Intent(
                    this,
                    CatalogoLibrosActivity::class.java
                )
            startActivity(intent)
        }
        findViewById<androidx.cardview.widget.CardView>(R.id.btnPrestamos).setOnClickListener {
            val intent =
                Intent(
                    this,
                    MisPrestamosActivity::class.java
                )
            startActivity(intent)
        }
        findViewById<androidx.cardview.widget.CardView>(R.id.btnPerfil).setOnClickListener {
            val intent =
                Intent(
                    this,
                    PerfilUsuarioActivity::class.java
                )
            startActivity(intent)
        }
        findViewById<androidx.cardview.widget.CardView>(R.id.btnFavoritos).setOnClickListener {
            val intent =
                Intent(
                    this,
                    FavoritosActivity::class.java
                )
            startActivity(intent)
        }
        findViewById<androidx.cardview.widget.CardView>(R.id.btnAjustes).setOnClickListener {
            val intent =
                Intent(
                    this,
                    AjustesActivity::class.java
                )
            startActivity(intent)
        }
    }
}
