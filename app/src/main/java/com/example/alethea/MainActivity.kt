package com.example.alethea

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alethea.EdgarRosario.InicioActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent =
            Intent(
                this,
                InicioActivity::class.java
            )
        startActivity(intent)
        finish()
    }
}
