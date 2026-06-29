package com.example.alethea.DelgadoJhezrrel

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.alethea.EdgarRosario.InicioActivity
import com.example.alethea.MusicManager
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

        findViewById<ImageView>(R.id.btnVolver).setOnClickListener { finish() }
        findViewById<LinearLayout>(R.id.btnModoClaro).setOnClickListener { seleccionarModo(true) }
        findViewById<LinearLayout>(R.id.btnModoOscuro).setOnClickListener { seleccionarModo(false) }
        findViewById<LinearLayout>(R.id.btnCerrarSesion).setOnClickListener {
            MusicManager.detener()
            val intent =
                Intent(
                    this,
                    InicioActivity::class.java
                )
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val btnPlay = findViewById<ImageView>(R.id.btnPlay)
        val seekBar = findViewById<SeekBar>(R.id.seekBarVolumen)

        actualizarIconoPlay(btnPlay)

        btnPlay.setOnClickListener {
            if (MusicManager.estaReproduciendo()) {
                MusicManager.pausar()
            } else {
                MusicManager.iniciar(this)
            }
            actualizarIconoPlay(btnPlay)
        }

        seekBar.progress = (MusicManager.getVolumen() * 100).toInt()

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                MusicManager.setVolumen(progress / 100f)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun actualizarIconoPlay(btnPlay: ImageView) {
        btnPlay.setImageResource(
            if (MusicManager.estaReproduciendo()) R.drawable.ic_pausa
            else R.drawable.ic_play
        )
    }
}
