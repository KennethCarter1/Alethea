package com.example.alethea

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

object SessionManager {
    private const val PREFS_NAME = "alethea_prefs"

    private const val KEY_MODO_OSCURO = "modo_oscuro"
    private const val KEY_USUARIO_ID = "usuario_id"
    private const val KEY_USUARIO_NOMBRE = "usuario_nombre"
    private const val KEY_ES_ADMIN = "es_admin"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val oscuro = prefs?.getBoolean(KEY_MODO_OSCURO, false) ?: false
        AppCompatDelegate.setDefaultNightMode(
            if (oscuro) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun esModoOscuro(): Boolean = prefs?.getBoolean(KEY_MODO_OSCURO, false) ?: false

    fun setModoOscuro(oscuro: Boolean) {
        prefs?.edit()?.putBoolean(KEY_MODO_OSCURO, oscuro)?.apply()
    }

    fun guardarSesion(usuarioId: Int, nombre: String, admin: Boolean) {
        prefs?.edit()?.apply {
            putInt(KEY_USUARIO_ID, usuarioId)
            putString(KEY_USUARIO_NOMBRE, nombre)
            putBoolean(KEY_ES_ADMIN, admin)
            apply()
        }
    }

    fun getUsuarioId(): Int = prefs?.getInt(KEY_USUARIO_ID, -1) ?: -1

    fun getUsuarioNombre(): String = prefs?.getString(KEY_USUARIO_NOMBRE, "") ?: ""

    fun esAdmin(): Boolean = prefs?.getBoolean(KEY_ES_ADMIN, false) ?: false

    fun cerrarSesion() {
        prefs?.edit()?.apply {
            putInt(KEY_USUARIO_ID, -1)
            putString(KEY_USUARIO_NOMBRE, "")
            putBoolean(KEY_ES_ADMIN, false)
            apply()
        }
    }
}
