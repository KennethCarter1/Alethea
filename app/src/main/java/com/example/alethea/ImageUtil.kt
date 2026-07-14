package com.example.alethea

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File
import java.util.UUID

object ImageUtil {
    private const val IMAGE_DIR = "images"

    fun crearIntentSelector(): Intent {
        return Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/png", "image/jpeg"))
        }
    }

    fun copiarImagen(context: Context, uri: Uri): String? {
        return try {
            val dir = File(context.filesDir, IMAGE_DIR)
            if (!dir.exists()) dir.mkdirs()
            val nombre = "libro_${UUID.randomUUID()}.jpg"
            val archivo = File(dir, nombre)
            context.contentResolver.openInputStream(uri)?.use { input ->
                archivo.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            nombre
        } catch (e: Exception) {
            null
        }
    }

    fun getRuta(context: Context, nombre: String): String? {
        val archivo = File(context.filesDir, "$IMAGE_DIR/$nombre")
        return if (archivo.exists()) archivo.absolutePath else null
    }
}
