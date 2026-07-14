package com.example.alethea.CarterKenneth

import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.alethea.AletheaBd
import com.example.alethea.R
import com.example.alethea.SessionManager

class PerfilUsuarioActivity : AppCompatActivity() {
    private var usuarioId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kenneth_perfil_usuario)

        usuarioId = SessionManager.getUsuarioId()
        if (usuarioId == -1) {
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        findViewById<android.widget.ImageView>(R.id.btnAtras).setOnClickListener { finish() }

        val nacionalidades = listOf("Panameño", "Colombiano", "Mexicano", "Argentino", "Español", "Venezolano", "Peruano", "Chileno", "Costarricense", "Dominicano")
        findViewById<Spinner>(R.id.spNacionalidad).adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nacionalidades).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        cargarDatos(nacionalidades)

        findViewById<LinearLayout>(R.id.btnGuardarCambios).setOnClickListener {
            guardarCambios(nacionalidades)
        }

        findViewById<LinearLayout>(R.id.btnActualizarContrasena).setOnClickListener {
            actualizarContrasena()
        }

        togglePasswordVisibility(findViewById(R.id.etContrasenaActual), findViewById(R.id.ivOjoActual))
        togglePasswordVisibility(findViewById(R.id.etNuevaContrasena), findViewById(R.id.ivOjoNueva))
        togglePasswordVisibility(findViewById(R.id.etConfirmarContrasena), findViewById(R.id.ivOjoConfirmar))
    }

    private fun togglePasswordVisibility(et: EditText, iv: ImageView) {
        iv.setOnClickListener {
            if (et.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                et.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                iv.setImageResource(R.drawable.ic_visibilidad)
            } else {
                et.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                iv.setImageResource(R.drawable.ic_visibilidad_off)
            }
            et.text?.let { et.setSelection(it.length) }
        }
    }

    private fun cargarDatos(nacionalidades: List<String>) {
        val db = AletheaBd(this).readableDatabase
        val cursor = db.rawQuery(
            "SELECT nombre, apellido, correo, cedula, nacionalidad, usuario FROM Usuarios WHERE id = ?",
            arrayOf(usuarioId.toString())
        )
        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(0) ?: ""
            val apellido = cursor.getString(1) ?: ""
            findViewById<EditText>(R.id.etNombre).setText(nombre)
            findViewById<EditText>(R.id.etApellido).setText(apellido)
            findViewById<EditText>(R.id.etCorreo).setText(cursor.getString(2) ?: "")
            findViewById<EditText>(R.id.etCedula).setText(cursor.getString(3) ?: "")
            val nac = cursor.getString(4) ?: ""
            val idx = nacionalidades.indexOfFirst { it.equals(nac, ignoreCase = true) }
            if (idx >= 0) findViewById<Spinner>(R.id.spNacionalidad).setSelection(idx)
            findViewById<TextView>(R.id.tvNombrePerfil).text = "$nombre $apellido"
        }
        cursor.close()

        val totalPrestamos = db.rawQuery(
            "SELECT COUNT(*) FROM Prestamos WHERE usuario_id = ?", arrayOf(usuarioId.toString())
        ).let { c -> c.moveToFirst().let { c.getInt(0) }.also { c.close() } }

        val totalFavoritos = db.rawQuery(
            "SELECT COUNT(*) FROM Favoritos WHERE usuario_id = ?", arrayOf(usuarioId.toString())
        ).let { c -> c.moveToFirst().let { c.getInt(0) }.also { c.close() } }

        val totalPendientes = db.rawQuery(
            "SELECT COUNT(*) FROM Prestamos WHERE usuario_id = ? AND estado = 'Pendiente'",
            arrayOf(usuarioId.toString())
        ).let { c -> c.moveToFirst().let { c.getInt(0) }.also { c.close() } }

        db.close()

        findViewById<TextView>(R.id.tvTotalPrestamos).text = totalPrestamos.toString().padStart(2, '0')
        findViewById<TextView>(R.id.tvTotalFavoritos).text = totalFavoritos.toString().padStart(2, '0')
        findViewById<TextView>(R.id.tvTotalPendientes).text = totalPendientes.toString().padStart(2, '0')
    }

    private fun guardarCambios(nacionalidades: List<String>) {
        val nombre = findViewById<EditText>(R.id.etNombre).text.toString().trim()
        val apellido = findViewById<EditText>(R.id.etApellido).text.toString().trim()
        val correo = findViewById<EditText>(R.id.etCorreo).text.toString().trim()
        val cedula = findViewById<EditText>(R.id.etCedula).text.toString().trim()
        val nacionalidad = nacionalidades[findViewById<Spinner>(R.id.spNacionalidad).selectedItemPosition]

        if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || cedula.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show(); return
        }
        if (!Regex("^[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ]+$").matches(nombre)) {
            Toast.makeText(this, "El nombre solo debe contener letras", Toast.LENGTH_SHORT).show(); return
        }
        if (!Regex("^[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ]+$").matches(apellido)) {
            Toast.makeText(this, "El apellido solo debe contener letras", Toast.LENGTH_SHORT).show(); return
        }
        if (!Regex("^[^@]+@[^@]+\\.[^@]+$").matches(correo)) {
            Toast.makeText(this, "Correo inválido (ej: algo@algo.com)", Toast.LENGTH_SHORT).show(); return
        }
        if (!Regex("^(?:\\d{1,2}|[EN]|PE)-\\d{1,4}-\\d{1,4}$").matches(cedula)) {
            Toast.makeText(this, "Cédula inválida (ej: 8-1234-567 o E-8-1234)", Toast.LENGTH_SHORT).show(); return
        }

        val db = AletheaBd(this).writableDatabase
        db.execSQL(
            "UPDATE Usuarios SET nombre = ?, apellido = ?, correo = ?, cedula = ?, nacionalidad = ? WHERE id = ?",
            arrayOf(nombre, apellido, correo, cedula, nacionalidad, usuarioId.toString())
        )
        db.close()

        SessionManager.guardarSesion(usuarioId, "$nombre $apellido", SessionManager.esAdmin())
        Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show()
    }

    private fun actualizarContrasena() {
        val actual = findViewById<EditText>(R.id.etContrasenaActual).text.toString()
        val nueva = findViewById<EditText>(R.id.etNuevaContrasena).text.toString()
        val confirmar = findViewById<EditText>(R.id.etConfirmarContrasena).text.toString()

        if (actual.isEmpty() || nueva.isEmpty() || confirmar.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        if (nueva != confirmar) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        val db = AletheaBd(this).readableDatabase
        val cursor = db.rawQuery(
            "SELECT contrasena FROM Usuarios WHERE id = ?", arrayOf(usuarioId.toString())
        )
        if (cursor.moveToFirst() && cursor.getString(0) == actual) {
            cursor.close()
            db.close()

            val dbw = AletheaBd(this).writableDatabase
            dbw.execSQL("UPDATE Usuarios SET contrasena = ? WHERE id = ?", arrayOf(nueva, usuarioId.toString()))
            dbw.close()
            Toast.makeText(this, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
            findViewById<EditText>(R.id.etContrasenaActual).setText("")
            findViewById<EditText>(R.id.etNuevaContrasena).setText("")
            findViewById<EditText>(R.id.etConfirmarContrasena).setText("")
        } else {
            cursor.close()
            db.close()
            Toast.makeText(this, "La contraseña actual no es correcta", Toast.LENGTH_SHORT).show()
        }
    }
}
