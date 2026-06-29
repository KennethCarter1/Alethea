package com.example.alethea.DelgadoJhezrrel

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.alethea.EdgarRosario.InicioActivity
import com.example.alethea.R

class AjustesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_jhezrrel_ajustes)
        supportActionBar?.hide()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val radioClaro = findViewById<View>(R.id.radioClaro)
        val radioOscuro = findViewById<View>(R.id.radioOscuro)
        fun seleccionarModo(claro: Boolean) {
            radioClaro.setBackgroundResource(if (claro) R.drawable.radio_activo else R.drawable.radio_inactivo)
            radioOscuro.setBackgroundResource(if (claro) R.drawable.radio_inactivo else R.drawable.radio_activo)
            Toast.makeText(this, if (claro) "Modo claro seleccionado" else "Modo oscuro seleccionado", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.btnVolver).setOnClickListener { finish() }
        findViewById<LinearLayout>(R.id.btnModoClaro).setOnClickListener { seleccionarModo(true) }
        findViewById<LinearLayout>(R.id.btnModoOscuro).setOnClickListener { seleccionarModo(false) }
        findViewById<LinearLayout>(R.id.btnCerrarSesion).setOnClickListener {
            val intent = Intent(this, InicioActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
