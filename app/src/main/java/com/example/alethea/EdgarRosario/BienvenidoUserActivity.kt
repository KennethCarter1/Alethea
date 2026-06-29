package com.example.alethea.EdgarRosario

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alethea.CarterKenneth.CatalogoLibrosActivity
import com.example.alethea.CarterKenneth.FavoritosActivity
import com.example.alethea.CarterKenneth.MisPrestamosActivity
import com.example.alethea.CarterKenneth.PerfilUsuarioActivity
import com.example.alethea.DelgadoJhezrrel.AjustesActivity
import com.example.alethea.R

class BienvenidoUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edgar_bienvenido_user)

        findViewById<androidx.cardview.widget.CardView>(R.id.btnCatalogo).setOnClickListener {
            startActivity(Intent(this, CatalogoLibrosActivity::class.java))
        }
        findViewById<androidx.cardview.widget.CardView>(R.id.btnPrestamos).setOnClickListener {
            startActivity(Intent(this, MisPrestamosActivity::class.java))
        }
        findViewById<androidx.cardview.widget.CardView>(R.id.btnPerfil).setOnClickListener {
            startActivity(Intent(this, PerfilUsuarioActivity::class.java))
        }
        findViewById<androidx.cardview.widget.CardView>(R.id.btnFavoritos).setOnClickListener {
            startActivity(Intent(this, FavoritosActivity::class.java))
        }
        findViewById<androidx.cardview.widget.CardView>(R.id.btnAjustes).setOnClickListener {
            startActivity(Intent(this, AjustesActivity::class.java))
        }
    }
}
