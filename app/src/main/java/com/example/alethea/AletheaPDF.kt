package com.example.alethea

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AletheaPDF {

    private const val ETIQUETA_LOG = "AletheaPDF"
    private const val LIMITE_PENDIENTES = 8
    private const val LIMITE_ACTIVOS = 12

    // Representa una cantidad que se mostrará en una barra del reporte.
    private data class Conteo(
        val etiqueta: String,
        val cantidad: Int,
        val color: Int
    )

    // Guarda cada posición de las clasificaciones de libros y usuarios.
    private data class Clasificacion(
        val principal: String,
        val secundario: String,
        val cantidad: Int
    )

    // Contiene los datos que se mostrarán en una fila de préstamo.
    private data class PrestamoFila(
        val usuario: String,
        val libro: String,
        val fecha: String,
        val estado: String = ""
    )

    // Agrupa toda la información consultada antes de comenzar a dibujar el PDF.
    private data class DatosReporte(
        val totalUsuarios: Int,
        val totalLibros: Int,
        val totalAutores: Int,
        val totalCategorias: Int,
        val totalSolicitudes: Int,
        val totalFavoritos: Int,
        val librosPrestados: Int,
        val pendientes: Int,
        val aceptadosVigentes: Int,
        val atrasados: Int,
        val rechazados: Int,
        val devueltos: Int,
        val categorias: List<Conteo>,
        val librosMasPrestados: List<Clasificacion>,
        val usuariosConMasPrestamos: List<Clasificacion>,
        val librosMasFavoritos: List<Clasificacion>,
        val solicitudesPorMes: List<Conteo>,
        val prestamosPendientes: List<PrestamoFila>,
        val prestamosActivos: List<PrestamoFila>
    )

    // Genera el PDF en la ubicación elegida y devuelve si la exportación terminó bien.
    fun generarReporte(contexto: Context, destino: Uri): Boolean {
        val documento = PdfDocument()

        return try {
            val datos = cargarDatos(contexto)
            val fecha = SimpleDateFormat(
                "dd 'de' MMMM 'de' yyyy 'a las' HH:mm",
                Locale.forLanguageTag("es-PA")
            ).format(Date())

            val generador = GeneradorReporte(contexto, documento, fecha)
            generador.dibujar(datos)

            val salida = contexto.contentResolver.openOutputStream(destino, "w")
            if (salida == null) {
                false
            } else {
                salida.use { flujoSalida -> documento.writeTo(flujoSalida) }
                true
            }
        } catch (error: Exception) {
            Log.e(ETIQUETA_LOG, "No se pudo generar el reporte PDF", error)
            false
        } finally {
            try {
                documento.close()
            } catch (_: Exception) {
            }
        }
    }

    // Consulta una sola vez la base de datos y prepara los datos resumidos del reporte.
    private fun cargarDatos(contexto: Context): DatosReporte {
        val ayudanteBD = AletheaBd(contexto)

        try {
            val baseDatos = ayudanteBD.readableDatabase
            val totalUsuarios = consultarEntero(baseDatos, "SELECT COUNT(*) FROM Usuarios")
            val totalLibros = consultarEntero(baseDatos, "SELECT COALESCE(SUM(stock), 0) FROM Libros")
            val totalAutores = consultarEntero(
                baseDatos,
                "SELECT COUNT(DISTINCT autor_libro) FROM Libros"
            )
            val totalCategorias = consultarEntero(
                baseDatos,
                "SELECT COUNT(DISTINCT categoria) FROM Libros"
            )
            val totalSolicitudes = consultarEntero(baseDatos, "SELECT COUNT(*) FROM Prestamos")
            val totalFavoritos = consultarEntero(baseDatos, "SELECT COUNT(*) FROM Favoritos")
            val librosPrestados = consultarEntero(
                baseDatos,
                "SELECT COUNT(*) FROM Prestamos WHERE estado = 'Aceptada'"
            )
            val pendientes = consultarEntero(
                baseDatos,
                "SELECT COUNT(*) FROM Prestamos WHERE estado = 'Pendiente'"
            )
            val aceptadosVigentes = consultarEntero(
                baseDatos,
                """SELECT COUNT(*) FROM Prestamos
                   WHERE estado = 'Aceptada'
                   AND (fecha_devolucion IS NULL OR fecha_devolucion >= date('now','localtime'))"""
            )
            val atrasados = consultarEntero(
                baseDatos,
                """SELECT COUNT(*) FROM Prestamos
                   WHERE estado = 'Aceptada'
                   AND fecha_devolucion IS NOT NULL
                   AND fecha_devolucion < date('now','localtime')"""
            )
            val rechazados = consultarEntero(
                baseDatos,
                "SELECT COUNT(*) FROM Prestamos WHERE estado = 'Rechazado'"
            )
            val devueltos = consultarEntero(
                baseDatos,
                "SELECT COUNT(*) FROM Prestamos WHERE estado = 'Devuelto'"
            )

            val categorias = cargarConteos(
                baseDatos,
                """SELECT categoria, COALESCE(SUM(stock), 0)
                   FROM Libros
                   GROUP BY categoria
                   ORDER BY SUM(stock) DESC, categoria""",
                Color.rgb(171, 120, 45)
            )

            val librosMasPrestados = cargarClasificacion(
                baseDatos,
                """SELECT l.nombre_libro, l.autor_libro, COUNT(*)
                   FROM Prestamos p
                   INNER JOIN Libros l ON p.libro_id = l.id
                   WHERE p.estado IN ('Aceptada', 'Devuelto')
                   GROUP BY p.libro_id
                   ORDER BY COUNT(*) DESC, l.nombre_libro
                   LIMIT 5"""
            )

            val usuariosConMasPrestamos = cargarClasificacion(
                baseDatos,
                """SELECT TRIM(u.nombre || ' ' || u.apellido), u.usuario, COUNT(*)
                   FROM Prestamos p
                   INNER JOIN Usuarios u ON p.usuario_id = u.id
                   WHERE p.estado IN ('Aceptada', 'Devuelto')
                   GROUP BY p.usuario_id
                   ORDER BY COUNT(*) DESC, u.nombre
                   LIMIT 5"""
            )

            val librosMasFavoritos = cargarClasificacion(
                baseDatos,
                """SELECT l.nombre_libro, l.autor_libro, COUNT(*)
                   FROM Favoritos f
                   INNER JOIN Libros l ON f.libro_id = l.id
                   GROUP BY f.libro_id
                   ORDER BY COUNT(*) DESC, l.nombre_libro
                   LIMIT 5"""
            )

            val solicitudesPorMes = cargarSolicitudesPorMes(baseDatos)
            val prestamosPendientes = cargarPrestamosPendientes(baseDatos)
            val prestamosActivos = cargarPrestamosActivos(baseDatos)

            return DatosReporte(
                totalUsuarios = totalUsuarios,
                totalLibros = totalLibros,
                totalAutores = totalAutores,
                totalCategorias = totalCategorias,
                totalSolicitudes = totalSolicitudes,
                totalFavoritos = totalFavoritos,
                librosPrestados = librosPrestados,
                pendientes = pendientes,
                aceptadosVigentes = aceptadosVigentes,
                atrasados = atrasados,
                rechazados = rechazados,
                devueltos = devueltos,
                categorias = categorias,
                librosMasPrestados = librosMasPrestados,
                usuariosConMasPrestamos = usuariosConMasPrestamos,
                librosMasFavoritos = librosMasFavoritos,
                solicitudesPorMes = solicitudesPorMes,
                prestamosPendientes = prestamosPendientes,
                prestamosActivos = prestamosActivos
            )
        } finally {
            ayudanteBD.close()
        }
    }

    // Ejecuta una consulta que devuelve una sola cantidad.
    private fun consultarEntero(baseDatos: SQLiteDatabase, consulta: String): Int {
        baseDatos.rawQuery(consulta, null).use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.getInt(0)
            }
        }
        return 0
    }

    // Convierte una consulta agrupada en valores que pueden dibujarse como barras.
    private fun cargarConteos(
        baseDatos: SQLiteDatabase,
        consulta: String,
        color: Int
    ): List<Conteo> {
        val resultado = mutableListOf<Conteo>()
        baseDatos.rawQuery(consulta, null).use { cursor ->
            while (cursor.moveToNext()) {
                resultado.add(
                    Conteo(
                        etiqueta = cursor.getString(0) ?: "Sin especificar",
                        cantidad = cursor.getInt(1),
                        color = color
                    )
                )
            }
        }
        return resultado
    }

    // Carga las cinco primeras posiciones para una clasificación del reporte.
    private fun cargarClasificacion(
        baseDatos: SQLiteDatabase,
        consulta: String
    ): List<Clasificacion> {
        val resultado = mutableListOf<Clasificacion>()
        baseDatos.rawQuery(consulta, null).use { cursor ->
            while (cursor.moveToNext()) {
                resultado.add(
                    Clasificacion(
                        principal = cursor.getString(0) ?: "Sin nombre",
                        secundario = cursor.getString(1) ?: "",
                        cantidad = cursor.getInt(2)
                    )
                )
            }
        }
        return resultado
    }

    // Agrupa las solicitudes de los últimos doce meses disponibles.
    private fun cargarSolicitudesPorMes(baseDatos: SQLiteDatabase): List<Conteo> {
        val resultado = mutableListOf<Conteo>()
        val consulta = """SELECT substr(fecha_prestamo, 1, 7) AS mes, COUNT(*)
                     FROM Prestamos
                     WHERE length(fecha_prestamo) >= 7
                     GROUP BY mes
                     ORDER BY mes DESC
                     LIMIT 12"""

        baseDatos.rawQuery(consulta, null).use { cursor ->
            while (cursor.moveToNext()) {
                resultado.add(
                    Conteo(
                        etiqueta = nombreMes(cursor.getString(0) ?: ""),
                        cantidad = cursor.getInt(1),
                        color = Color.rgb(92, 63, 28)
                    )
                )
            }
        }
        resultado.reverse()
        return resultado
    }

    // Convierte una fecha YYYY-MM en un nombre de mes fácil de leer.
    private fun nombreMes(valor: String): String {
        val partes = valor.split("-")
        if (partes.size != 2) {
            return valor
        }

        val numeroMes = partes[1].toIntOrNull()
        val meses = arrayOf(
            "", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
        if (numeroMes == null || numeroMes !in 1..12) {
            return valor
        }
        return "${meses[numeroMes]} ${partes[0]}"
    }

    // Obtiene las solicitudes pendientes más recientes para el seguimiento administrativo.
    private fun cargarPrestamosPendientes(baseDatos: SQLiteDatabase): List<PrestamoFila> {
        val resultado = mutableListOf<PrestamoFila>()
        val consulta = """SELECT TRIM(u.nombre || ' ' || u.apellido),
                            l.nombre_libro,
                            p.fecha_prestamo
                     FROM Prestamos p
                     INNER JOIN Usuarios u ON p.usuario_id = u.id
                     INNER JOIN Libros l ON p.libro_id = l.id
                     WHERE p.estado = 'Pendiente'
                     ORDER BY p.id DESC
                     LIMIT $LIMITE_PENDIENTES"""

        baseDatos.rawQuery(consulta, null).use { cursor ->
            while (cursor.moveToNext()) {
                resultado.add(
                    PrestamoFila(
                        usuario = cursor.getString(0) ?: "Sin nombre",
                        libro = cursor.getString(1) ?: "Sin libro",
                        fecha = cursor.getString(2) ?: ""
                    )
                )
            }
        }
        return resultado
    }

    // Obtiene préstamos aceptados y calcula cuáles ya están atrasados por su fecha límite.
    private fun cargarPrestamosActivos(baseDatos: SQLiteDatabase): List<PrestamoFila> {
        val resultado = mutableListOf<PrestamoFila>()
        val consulta = """SELECT TRIM(u.nombre || ' ' || u.apellido),
                            l.nombre_libro,
                            COALESCE(p.fecha_devolucion, 'Sin fecha'),
                            CASE
                                WHEN p.fecha_devolucion IS NOT NULL
                                     AND p.fecha_devolucion < date('now')
                                THEN 'Atrasado'
                                ELSE 'Aceptada'
                            END
                     FROM Prestamos p
                     INNER JOIN Usuarios u ON p.usuario_id = u.id
                     INNER JOIN Libros l ON p.libro_id = l.id
                     WHERE p.estado = 'Aceptada'
                     ORDER BY CASE WHEN p.fecha_devolucion < date('now') THEN 0 ELSE 1 END,
                              p.id DESC
                     LIMIT $LIMITE_ACTIVOS"""

        baseDatos.rawQuery(consulta, null).use { cursor ->
            while (cursor.moveToNext()) {
                resultado.add(
                    PrestamoFila(
                        usuario = cursor.getString(0) ?: "Sin nombre",
                        libro = cursor.getString(1) ?: "Sin libro",
                        fecha = cursor.getString(2) ?: "",
                        estado = cursor.getString(3) ?: "Aceptada"
                    )
                )
            }
        }
        return resultado
    }

    // Se encarga de dibujar todas las secciones y administrar la paginación automática.
    private class GeneradorReporte(
        contexto: Context,
        private val documento: PdfDocument,
        private val fecha: String
    ) {
        private companion object {
            const val ANCHO_PAGINA = 595
            const val ALTO_PAGINA = 842
            const val MARGEN = 36f
            const val ANCHO_CONTENIDO = ANCHO_PAGINA - MARGEN * 2f
            const val LIMITE_CONTENIDO = 790f
            const val ALTO_FILA = 24f

            val COLOR_PRIMARIO = Color.rgb(92, 63, 28)
            val COLOR_DORADO = Color.rgb(185, 132, 42)
            val COLOR_CREMA = Color.rgb(249, 246, 238)
            val COLOR_TEXTO = Color.rgb(48, 44, 39)
            val COLOR_SUAVE = Color.rgb(113, 105, 95)
            val COLOR_LINEA = Color.rgb(220, 207, 187)
            val COLOR_VERDE = Color.rgb(46, 125, 50)
            val COLOR_NARANJA = Color.rgb(239, 108, 0)
            val COLOR_ROJO = Color.rgb(198, 40, 40)
        }

        private val logo: Bitmap? = BitmapFactory.decodeResource(
            contexto.resources,
            R.drawable.logo_claro
        )
        private var paginaActual: PdfDocument.Page? = null
        private lateinit var lienzo: Canvas
        private var numeroPagina = 0
        private var y = MARGEN

        // Dibuja únicamente las secciones que tienen información y finaliza el documento.
        fun dibujar(datos: DatosReporte) {
            nuevaPagina()

            val disponibles = (datos.totalLibros - datos.librosPrestados).coerceAtLeast(0)
            dibujarMetricas(
                listOf(
                    "Usuarios" to datos.totalUsuarios,
                    "Ejemplares" to datos.totalLibros,
                    "Autores" to datos.totalAutores,
                    "Categorías" to datos.totalCategorias,
                    "Solicitudes" to datos.totalSolicitudes,
                    "Favoritos" to datos.totalFavoritos,
                    "Disponibles" to disponibles,
                    "Prestados" to datos.librosPrestados
                )
            )

            dibujarBarras(
                "Estado de las solicitudes",
                listOf(
                    Conteo("Pendientes", datos.pendientes, COLOR_NARANJA),
                    Conteo("Aceptadas vigentes", datos.aceptadosVigentes, COLOR_VERDE),
                    Conteo("Atrasadas", datos.atrasados, COLOR_ROJO),
                    Conteo("Rechazadas", datos.rechazados, COLOR_ROJO),
                    Conteo("Devueltas", datos.devueltos, COLOR_VERDE)
                )
            )

            dibujarBarras("Libros por categoría", datos.categorias)
            dibujarClasificacion(
                "Libros más prestados",
                datos.librosMasPrestados,
                "préstamo"
            )
            dibujarClasificacion(
                "Usuarios con más préstamos",
                datos.usuariosConMasPrestamos,
                "préstamo"
            )
            dibujarClasificacion(
                "Libros más guardados en favoritos",
                datos.librosMasFavoritos,
                "favorito"
            )
            dibujarBarras("Solicitudes por mes", datos.solicitudesPorMes)

            val filasPendientes = datos.prestamosPendientes.map { prestamo ->
                listOf(prestamo.usuario, prestamo.libro, prestamo.fecha)
            }
            dibujarTabla(
                titulo = "Solicitudes pendientes",
                encabezados = listOf("USUARIO", "LIBRO", "FECHA"),
                anchos = listOf(145f, 285f, 93f),
                filas = filasPendientes,
                totalReal = datos.pendientes
            )

            val filasActivas = datos.prestamosActivos.map { prestamo ->
                listOf(prestamo.usuario, prestamo.libro, prestamo.fecha, prestamo.estado)
            }
            dibujarTabla(
                titulo = "Préstamos activos y atrasados",
                encabezados = listOf("USUARIO", "LIBRO", "VENCE", "ESTADO"),
                anchos = listOf(130f, 205f, 95f, 93f),
                filas = filasActivas,
                totalReal = datos.aceptadosVigentes + datos.atrasados
            )

            dibujarNotaFinal()
            terminarPagina()
            logo?.recycle()
        }

        // Crea una página y dibuja el encabezado correspondiente.
        private fun nuevaPagina() {
            numeroPagina++
            val informacionPagina = PdfDocument.PageInfo.Builder(
                ANCHO_PAGINA,
                ALTO_PAGINA,
                numeroPagina
            ).create()
            paginaActual = documento.startPage(informacionPagina)
            lienzo = paginaActual!!.canvas

            if (numeroPagina == 1) {
                dibujarEncabezadoPrincipal()
            } else {
                dibujarEncabezadoContinuacion()
            }
        }

        // Dibuja el logo, el título y la fecha en la primera página.
        private fun dibujarEncabezadoPrincipal() {
            val fondo = pinturaRelleno(COLOR_CREMA)
            lienzo.drawRoundRect(
                RectF(MARGEN, 28f, ANCHO_PAGINA - MARGEN, 140f),
                14f,
                14f,
                fondo
            )

            dibujarLogo(RectF(MARGEN + 12f, 39f, MARGEN + 102f, 129f))
            lienzo.drawText(
                "Reporte de estadísticas",
                MARGEN + 122f,
                69f,
                pinturaTexto(23f, COLOR_PRIMARIO, true)
            )
            lienzo.drawText(
                "Sistema de Gestión de Biblioteca",
                MARGEN + 122f,
                93f,
                pinturaTexto(12f, COLOR_TEXTO)
            )
            lienzo.drawText(
                "Generado el $fecha",
                MARGEN + 122f,
                116f,
                pinturaTexto(10f, COLOR_SUAVE)
            )
            lienzo.drawLine(
                MARGEN,
                150f,
                ANCHO_PAGINA - MARGEN,
                150f,
                pinturaLinea(COLOR_DORADO, 1.5f)
            )
            y = 164f
        }

        // Dibuja un encabezado reducido en las páginas siguientes.
        private fun dibujarEncabezadoContinuacion() {
            dibujarLogo(RectF(MARGEN, 24f, MARGEN + 38f, 62f))
            lienzo.drawText(
                "Reporte de estadísticas",
                MARGEN + 50f,
                40f,
                pinturaTexto(15f, COLOR_PRIMARIO, true)
            )
            lienzo.drawText(
                "Generado el $fecha",
                MARGEN + 50f,
                57f,
                pinturaTexto(8.5f, COLOR_SUAVE)
            )
            lienzo.drawLine(
                MARGEN,
                70f,
                ANCHO_PAGINA - MARGEN,
                70f,
                pinturaLinea(COLOR_LINEA, 1f)
            )
            y = 84f
        }

        // Coloca el logo de Alethea conservando su transparencia.
        private fun dibujarLogo(destino: RectF) {
            val imagen = logo
            if (imagen != null) {
                val pinturaLogo = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
                lienzo.drawBitmap(imagen, null, destino, pinturaLogo)
            }
        }

        // Presenta los indicadores generales en tarjetas compactas.
        private fun dibujarMetricas(metricas: List<Pair<String, Int>>) {
            val columnas = 4
            val espacio = 8f
            val altoTarjeta = 48f
            val filas = (metricas.size + columnas - 1) / columnas
            asegurarEspacio(31f + filas * (altoTarjeta + espacio))
            dibujarTituloSeccion("Resumen general")

            val anchoTarjeta = (ANCHO_CONTENIDO - espacio * (columnas - 1)) / columnas
            metricas.forEachIndexed { indice, metrica ->
                val columna = indice % columnas
                val fila = indice / columnas
                val izquierda = MARGEN + columna * (anchoTarjeta + espacio)
                val arriba = y + fila * (altoTarjeta + espacio)
                val rectangulo = RectF(
                    izquierda,
                    arriba,
                    izquierda + anchoTarjeta,
                    arriba + altoTarjeta
                )
                lienzo.drawRoundRect(rectangulo, 8f, 8f, pinturaRelleno(COLOR_CREMA))
                lienzo.drawRoundRect(rectangulo, 8f, 8f, pinturaLinea(COLOR_LINEA, 0.8f))

                val centro = rectangulo.centerX()
                lienzo.drawText(
                    metrica.second.toString(),
                    centro,
                    arriba + 21f,
                    pinturaTexto(17f, COLOR_PRIMARIO, true, Paint.Align.CENTER)
                )
                val pinturaEtiqueta = pinturaTexto(
                    8.5f,
                    COLOR_SUAVE,
                    false,
                    Paint.Align.CENTER
                )
                lienzo.drawText(
                    recortarTexto(metrica.first, anchoTarjeta - 10f, pinturaEtiqueta),
                    centro,
                    arriba + 39f,
                    pinturaEtiqueta
                )
            }
            y += filas * (altoTarjeta + espacio) + 4f
        }

        // Dibuja distribuciones y estados mediante barras proporcionales.
        private fun dibujarBarras(titulo: String, elementos: List<Conteo>) {
            if (elementos.isEmpty()) {
                return
            }

            asegurarEspacio(31f + ALTO_FILA)
            dibujarTituloSeccion(titulo)
            val maximo = elementos.maxOfOrNull { conteo -> conteo.cantidad }
                ?.coerceAtLeast(1) ?: 1
            val inicioBarra = MARGEN + 174f
            val anchoBarra = ANCHO_CONTENIDO - 220f

            elementos.forEach { elemento ->
                val salto = asegurarEspacio(ALTO_FILA)
                if (salto) {
                    dibujarTituloSeccion("$titulo (continuación)")
                }

                val pinturaEtiqueta = pinturaTexto(10f, COLOR_TEXTO)
                lienzo.drawText(
                    recortarTexto(elemento.etiqueta, 158f, pinturaEtiqueta),
                    MARGEN + 4f,
                    y + 15f,
                    pinturaEtiqueta
                )

                val fondo = RectF(inicioBarra, y + 7f, inicioBarra + anchoBarra, y + 17f)
                lienzo.drawRoundRect(fondo, 5f, 5f, pinturaRelleno(Color.rgb(235, 231, 224)))

                if (elemento.cantidad > 0) {
                    val progreso = anchoBarra * elemento.cantidad / maximo.toFloat()
                    val barra = RectF(inicioBarra, y + 7f, inicioBarra + progreso, y + 17f)
                    lienzo.drawRoundRect(barra, 5f, 5f, pinturaRelleno(elemento.color))
                }

                lienzo.drawText(
                    elemento.cantidad.toString(),
                    ANCHO_PAGINA - MARGEN,
                    y + 15f,
                    pinturaTexto(10f, COLOR_PRIMARIO, true, Paint.Align.RIGHT)
                )
                y += ALTO_FILA
            }
            y += 6f
        }

        // Dibuja una clasificación de hasta cinco elementos sin crear páginas vacías.
        private fun dibujarClasificacion(
            titulo: String,
            elementos: List<Clasificacion>,
            unidad: String
        ) {
            if (elementos.isEmpty()) {
                return
            }

            asegurarEspacio(31f + ALTO_FILA)
            dibujarTituloSeccion(titulo)
            elementos.forEachIndexed { indice, elemento ->
                val salto = asegurarEspacio(ALTO_FILA)
                if (salto) {
                    dibujarTituloSeccion("$titulo (continuación)")
                }

                lienzo.drawText(
                    "${indice + 1}.",
                    MARGEN + 4f,
                    y + 15f,
                    pinturaTexto(10f, COLOR_DORADO, true)
                )

                var descripcion = elemento.principal
                if (elemento.secundario.isNotBlank()) {
                    descripcion += " - ${elemento.secundario}"
                }
                val pinturaDescripcion = pinturaTexto(10f, COLOR_TEXTO)
                lienzo.drawText(
                    recortarTexto(descripcion, ANCHO_CONTENIDO - 130f, pinturaDescripcion),
                    MARGEN + 27f,
                    y + 15f,
                    pinturaDescripcion
                )

                var valor = "${elemento.cantidad} $unidad"
                if (elemento.cantidad != 1) {
                    valor += "s"
                }
                lienzo.drawText(
                    valor,
                    ANCHO_PAGINA - MARGEN,
                    y + 15f,
                    pinturaTexto(9.5f, COLOR_PRIMARIO, true, Paint.Align.RIGHT)
                )
                y += ALTO_FILA
            }
            y += 6f
        }

        // Dibuja tablas de seguimiento y repite su encabezado cuando cambia de página.
        private fun dibujarTabla(
            titulo: String,
            encabezados: List<String>,
            anchos: List<Float>,
            filas: List<List<String>>,
            totalReal: Int
        ) {
            if (filas.isEmpty()) {
                return
            }

            asegurarEspacio(31f + 22f + ALTO_FILA)
            dibujarTituloSeccion(titulo)
            dibujarEncabezadoTabla(encabezados, anchos)

            filas.forEachIndexed { indice, fila ->
                val salto = asegurarEspacio(ALTO_FILA)
                if (salto) {
                    dibujarTituloSeccion("$titulo (continuación)")
                    dibujarEncabezadoTabla(encabezados, anchos)
                }

                if (indice % 2 == 0) {
                    lienzo.drawRect(
                        MARGEN,
                        y,
                        ANCHO_PAGINA - MARGEN,
                        y + ALTO_FILA,
                        pinturaRelleno(Color.rgb(252, 250, 246))
                    )
                }

                var posicionX = MARGEN
                fila.forEachIndexed { columna, texto ->
                    val ancho = anchos[columna]
                    val pintura = pinturaTexto(9f, COLOR_TEXTO)
                    lienzo.drawText(
                        recortarTexto(texto, ancho - 10f, pintura),
                        posicionX + 5f,
                        y + 15f,
                        pintura
                    )
                    posicionX += ancho
                }
                lienzo.drawLine(
                    MARGEN,
                    y + ALTO_FILA,
                    ANCHO_PAGINA - MARGEN,
                    y + ALTO_FILA,
                    pinturaLinea(COLOR_LINEA, 0.45f)
                )
                y += ALTO_FILA
            }

            if (totalReal > filas.size) {
                asegurarEspacio(22f)
                val restantes = totalReal - filas.size
                lienzo.drawText(
                    "Se muestran los ${filas.size} más recientes. Hay $restantes registro(s) adicional(es).",
                    MARGEN + 5f,
                    y + 14f,
                    pinturaTexto(8.5f, COLOR_SUAVE)
                )
                y += 22f
            } else {
                y += 8f
            }
        }

        // Dibuja los nombres de las columnas de una tabla.
        private fun dibujarEncabezadoTabla(
            encabezados: List<String>,
            anchos: List<Float>
        ) {
            asegurarEspacio(22f)
            lienzo.drawRoundRect(
                RectF(MARGEN, y, ANCHO_PAGINA - MARGEN, y + 22f),
                5f,
                5f,
                pinturaRelleno(COLOR_CREMA)
            )
            var posicionX = MARGEN
            encabezados.forEachIndexed { indice, encabezado ->
                lienzo.drawText(
                    encabezado,
                    posicionX + 5f,
                    y + 14f,
                    pinturaTexto(8.5f, COLOR_PRIMARIO, true)
                )
                posicionX += anchos[indice]
            }
            y += 22f
        }

        // Añade una nota breve que indica el origen de la información.
        private fun dibujarNotaFinal() {
            asegurarEspacio(42f)
            val rectangulo = RectF(MARGEN, y + 4f, ANCHO_PAGINA - MARGEN, y + 36f)
            lienzo.drawRoundRect(rectangulo, 7f, 7f, pinturaRelleno(COLOR_CREMA))
            lienzo.drawText(
                "Este reporte resume la información registrada actualmente en Alethea.",
                MARGEN + 10f,
                y + 24f,
                pinturaTexto(9f, COLOR_SUAVE)
            )
            y += 42f
        }

        // Separa visualmente cada bloque con un título y una línea.
        private fun dibujarTituloSeccion(titulo: String) {
            asegurarEspacio(31f)
            lienzo.drawText(
                titulo,
                MARGEN,
                y + 15f,
                pinturaTexto(13f, COLOR_PRIMARIO, true)
            )
            lienzo.drawLine(
                MARGEN,
                y + 23f,
                ANCHO_PAGINA - MARGEN,
                y + 23f,
                pinturaLinea(COLOR_LINEA, 1f)
            )
            y += 31f
        }

        // Abre otra página solo cuando el siguiente contenido ya no cabe.
        private fun asegurarEspacio(altoNecesario: Float): Boolean {
            if (y + altoNecesario <= LIMITE_CONTENIDO) {
                return false
            }
            terminarPagina()
            nuevaPagina()
            return true
        }

        // Dibuja el pie con el número de página y cierra la página actual.
        private fun terminarPagina() {
            val pagina = paginaActual ?: return
            lienzo.drawLine(
                MARGEN,
                806f,
                ANCHO_PAGINA - MARGEN,
                806f,
                pinturaLinea(COLOR_LINEA, 0.8f)
            )
            lienzo.drawText(
                "Alethea - Reporte de estadísticas",
                MARGEN,
                823f,
                pinturaTexto(8f, COLOR_SUAVE)
            )
            lienzo.drawText(
                "Página $numeroPagina",
                ANCHO_PAGINA - MARGEN,
                823f,
                pinturaTexto(8f, COLOR_SUAVE, false, Paint.Align.RIGHT)
            )
            documento.finishPage(pagina)
            paginaActual = null
        }

        // Recorta textos largos con puntos suspensivos para evitar superposiciones.
        private fun recortarTexto(texto: String, anchoMaximo: Float, pintura: Paint): String {
            if (pintura.measureText(texto) <= anchoMaximo) {
                return texto
            }

            val puntos = "..."
            var recortado = texto
            while (recortado.isNotEmpty() && pintura.measureText(recortado + puntos) > anchoMaximo) {
                recortado = recortado.dropLast(1)
            }
            return recortado.trimEnd() + puntos
        }

        // Crea una pintura uniforme para todo el texto del documento.
        private fun pinturaTexto(
            tamano: Float,
            color: Int,
            negrita: Boolean = false,
            alineacion: Paint.Align = Paint.Align.LEFT
        ): Paint {
            val estilo = if (negrita) Typeface.BOLD else Typeface.NORMAL
            return Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = tamano
                this.color = color
                typeface = Typeface.create("sans-serif", estilo)
                textAlign = alineacion
            }
        }

        // Crea una pintura para fondos sólidos y tarjetas.
        private fun pinturaRelleno(color: Int): Paint {
            return Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.color = color
                style = Paint.Style.FILL
            }
        }

        // Crea una pintura para bordes y líneas divisorias.
        private fun pinturaLinea(color: Int, ancho: Float): Paint {
            return Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.color = color
                style = Paint.Style.STROKE
                strokeWidth = ancho
            }
        }
    }
}
