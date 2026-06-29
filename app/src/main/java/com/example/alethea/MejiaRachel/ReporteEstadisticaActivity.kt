package com.example.alethea.MejiaRachel

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alethea.R

class ReporteEstadisticaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rachel_reporte_estadistica)
        findViewById<android.widget.ImageView>(R.id.btnAtras).setOnClickListener { finish() }
    }
}
