package com.example.alethea.EdgarRosario

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alethea.DelgadoJhezrrel.AjustesActivity
import com.example.alethea.DelgadoJhezrrel.GestionAutoresActivity
import com.example.alethea.DelgadoJhezrrel.GestionUsuariosActivity
import com.example.alethea.MejiaRachel.GestionLibrosActivity
import com.example.alethea.MejiaRachel.PrestamosDevolucionesActivity
import com.example.alethea.MejiaRachel.ReporteEstadisticaActivity
import com.example.alethea.R

class BienvenidoAdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edgar_bienvenido_admin)

        findViewById<androidx.cardview.widget.CardView>(R.id.btnGestionLibros).setOnClickListener {
            startActivity(Intent(this, GestionLibrosActivity::class.java))
        }
        findViewById<androidx.cardview.widget.CardView>(R.id.btnPrestamosAdmin).setOnClickListener {
            startActivity(Intent(this, PrestamosDevolucionesActivity::class.java))
        }
        findViewById<androidx.cardview.widget.CardView>(R.id.btnGestionAutores).setOnClickListener {
            startActivity(Intent(this, GestionAutoresActivity::class.java))
        }
        findViewById<androidx.cardview.widget.CardView>(R.id.btnReportes).setOnClickListener {
            startActivity(Intent(this, ReporteEstadisticaActivity::class.java))
        }
        findViewById<androidx.cardview.widget.CardView>(R.id.btnGestionUsuarios).setOnClickListener {
            startActivity(Intent(this, GestionUsuariosActivity::class.java))
        }
        findViewById<androidx.cardview.widget.CardView>(R.id.btnAjustesAdmin).setOnClickListener {
            startActivity(Intent(this, AjustesActivity::class.java))
        }
    }
}
