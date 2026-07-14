package com.example.alethea

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AletheaBd(private val context: Context) : SQLiteOpenHelper(
    context,
    "Alethea.db",
    null,
    11
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE Usuarios(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                apellido TEXT,
                correo TEXT,
                cedula TEXT,
                nacionalidad TEXT,
                usuario TEXT NOT NULL,
                contrasena TEXT NOT NULL,
                es_admin INTEGER DEFAULT 0
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
                ruta_imagen TEXT,
                stock INTEGER NOT NULL DEFAULT 1
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
                fecha_entrega TEXT,
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

        db.execSQL("INSERT INTO Usuarios(nombre, apellido, correo, cedula, nacionalidad, usuario, contrasena, es_admin) VALUES('Admin', 'Principal', 'admin@alethea.com', '', '', 'admin', '1gs231', 1)")
        db.execSQL("INSERT INTO Usuarios(nombre, apellido, correo, cedula, nacionalidad, usuario, contrasena, es_admin) VALUES('Rachel', 'Mejia', 'rachel@alethea.com', '', '', 'rachelmejia', 'rachelmejia', 0)")
        db.execSQL("INSERT INTO Usuarios(nombre, apellido, correo, cedula, nacionalidad, usuario, contrasena, es_admin) VALUES('Edgar', 'Rosario', 'edgar@alethea.com', '', '', 'edgarrosario', 'edgarrosario', 0)")
        db.execSQL("INSERT INTO Usuarios(nombre, apellido, correo, cedula, nacionalidad, usuario, contrasena, es_admin) VALUES('Kenneth', 'Carter', 'kenneth@alethea.com', '', '', 'kennethcarter', 'kennethcarter', 0)")
        db.execSQL("INSERT INTO Usuarios(nombre, apellido, correo, cedula, nacionalidad, usuario, contrasena, es_admin) VALUES('Jhezrel', 'Delgado', 'jhezrel@alethea.com', '', '', 'jhezrreldelgado', 'jhezrreldelgado', 0)")

        DatosIniciales.insertar(db, context)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        if (oldVersion < 8) {
            db.execSQL("DROP TABLE IF EXISTS Favoritos")
            db.execSQL("DROP TABLE IF EXISTS Prestamos")
            db.execSQL("DROP TABLE IF EXISTS Libros")
            db.execSQL("DROP TABLE IF EXISTS Usuarios")
            onCreate(db)
            return
        }

        if (oldVersion < 9) {
            db.execSQL("ALTER TABLE Libros ADD COLUMN stock INTEGER NOT NULL DEFAULT 1")
        }

        if (oldVersion < 10) {
            db.execSQL("ALTER TABLE Prestamos ADD COLUMN fecha_entrega TEXT")
        }

        if (oldVersion < 11) {
            DatosIniciales.actualizarDatosExistentes(db)
        }
    }
}
