package com.example.alethea.DelgadoJhezrrel

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.alethea.EdgarRosario.InicioActivity
import com.example.alethea.MusicManager
import com.example.alethea.R
import com.example.alethea.SessionManager

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
        val opcionClaro = findViewById<LinearLayout>(R.id.btnModoClaro)
        val opcionOscuro = findViewById<LinearLayout>(R.id.btnModoOscuro)

        fun actualizarRadios() {
            val oscuro = SessionManager.esModoOscuro()
            radioClaro.setBackgroundResource(if (oscuro) R.drawable.jhez_radio_inactivo else R.drawable.jhez_radio_activo)
            radioOscuro.setBackgroundResource(if (oscuro) R.drawable.jhez_radio_activo else R.drawable.jhez_radio_inactivo)
            opcionClaro.isSelected = !oscuro
            opcionOscuro.isSelected = oscuro
        }

        fun seleccionarModo(claro: Boolean) {
            val oscuro = !claro
            SessionManager.setModoOscuro(oscuro)
            AppCompatDelegate.setDefaultNightMode(
                if (oscuro) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            actualizarRadios()
        }

        actualizarRadios()

        findViewById<ImageView>(R.id.btnVolver).setOnClickListener { finish() }
        opcionClaro.setOnClickListener { seleccionarModo(true) }
        opcionOscuro.setOnClickListener { seleccionarModo(false) }
        findViewById<LinearLayout>(R.id.btnCerrarSesion).setOnClickListener {
            SessionManager.cerrarSesion()
            MusicManager.detener()
            val intent = Intent(this, InicioActivity::class.java)
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
