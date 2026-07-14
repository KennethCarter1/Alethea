package com.example.alethea

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.alethea", appContext.packageName)
    }

    @Test
    fun verificaStockFavoritosYPrestamosIniciales() {
        val contexto = InstrumentationRegistry.getInstrumentation().targetContext
        val ayudante = AletheaBd(contexto)
        val db = ayudante.readableDatabase

        assertEquals(11, db.version)
        assertTrue(consultarCantidad(db, "SELECT COUNT(*) FROM Libros WHERE stock > 1") >= 20)
        assertTrue(consultarCantidad(db, "SELECT COUNT(DISTINCT stock) FROM Libros") > 1)

        db.rawQuery("SELECT id FROM Usuarios WHERE es_admin = 0", null).use { usuarios ->
            assertTrue(usuarios.count > 0)
            while (usuarios.moveToNext()) {
                val usuarioId = usuarios.getInt(0)
                assertTrue(consultarCantidad(db, "SELECT COUNT(*) FROM Favoritos WHERE usuario_id = $usuarioId") >= 2)
                assertTrue(consultarCantidad(db, "SELECT COUNT(*) FROM Prestamos WHERE usuario_id = $usuarioId AND estado = 'Pendiente'") >= 1)
                assertTrue(consultarCantidad(db, "SELECT COUNT(*) FROM Prestamos WHERE usuario_id = $usuarioId AND estado = 'Aceptada'") >= 1)
                assertTrue(consultarCantidad(db, "SELECT COUNT(*) FROM Prestamos WHERE usuario_id = $usuarioId AND estado = 'Rechazado'") >= 1)
            }
        }

        ayudante.close()
    }

    private fun consultarCantidad(db: android.database.sqlite.SQLiteDatabase, consulta: String): Int {
        db.rawQuery(consulta, null).use { cursor ->
            return if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
    }
}
