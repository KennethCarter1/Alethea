package com.example.alethea

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AletheaBd(context: Context) : SQLiteOpenHelper(
    context,
    "Alethea.db",
    null,
    1
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE Usuarios(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                correo TEXT NOT NULL,
                cedula TEXT NOT NULL,
                nacionalidad TEXT NOT NULL,
                usuario TEXT NOT NULL,
                contrasena TEXT NOT NULL,
                es_admin INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE Libros(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_libro TEXT NOT NULL,
                autor_libro TEXT NOT NULL,
                categoria TEXT NOT NULL,
                ano_creado TEXT NOT NULL,
                sinopsis TEXT NOT NULL,
                ruta_imagen TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE Prestamos(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario_id INTEGER NOT NULL,
                libro_id INTEGER NOT NULL,
                fecha_prestamo TEXT NOT NULL,
                fecha_devolucion TEXT,
                estado TEXT NOT NULL,
                FOREIGN KEY (usuario_id) REFERENCES Usuarios(id),
                FOREIGN KEY (libro_id) REFERENCES Libros(id)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE Favoritos(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario_id INTEGER NOT NULL,
                libro_id INTEGER NOT NULL,
                FOREIGN KEY (usuario_id) REFERENCES Usuarios(id),
                FOREIGN KEY (libro_id) REFERENCES Libros(id)
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO Usuarios(nombre, apellido, correo, cedula, nacionalidad, usuario, contrasena, es_admin)
            VALUES('kenneth', 'carter', 'kenntth@utp.ac.pa', '8-1001-1227', 'panamena', 'kenneth10', '10102003', 0)
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO Usuarios(nombre, apellido, correo, cedula, nacionalidad, usuario, contrasena, es_admin)
            VALUES('kenneth', 'carter', 'kenntth@utp.ac.pa', '8-1001-1227', 'panamena', 'kenneth01', '10102003', 1)
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS Favoritos")
        db.execSQL("DROP TABLE IF EXISTS Prestamos")
        db.execSQL("DROP TABLE IF EXISTS Libros")
        db.execSQL("DROP TABLE IF EXISTS Usuarios")
        onCreate(db)
    }
}
