package com.example.alethea

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.io.File
import kotlin.random.Random

object DatosIniciales {

    private data class LibroSeed(
        val nombre: String, val autor: String, val categoria: String,
        val anio: String, val sinopsis: String, val img: String, val stock: Int
    )

    private val libros = listOf(
        LibroSeed("Cien años de soledad", "Gabriel García Márquez", "Novela", "1967",
            "En Macondo, un pueblo fundado por José Arcadio Buendía, la familia Buendía vive siete generaciones de aventuras, amores imposibles, guerras civiles y milagros cotidianos. El realismo mágico se despliega en cada página mientras el tiempo circular y los destinos se entrelazan en una de las obras cumbre de la literatura universal. La llegada del gitano Melquíades y sus profecías, la insomnio colectivo, la peste del olvido y el nacimiento del último Buendía con cola de cerdo son solo algunas de las historias que conviven en esta novela inolvidable.",
            "15.jpg", 4),
        LibroSeed("El amor en los tiempos del cólera", "Gabriel García Márquez", "Novela", "1985",
            "Florentino Ariza y Fermina Daza se enamoran en la juventud, pero ella lo rechaza para casarse con el doctor Juvenal Urbino, un médico respetado. Florentino espera más de cincuenta años, nueve meses y cuatro días para declararle nuevamente su amor eterno. Durante ese tiempo acumula 622 aventuras amorosas, pero nunca deja de amar a Fermina. Tras la muerte de Urbino, Florentino renueva su cortejo en la vejez, demostrando que el amor no tiene edad ni plazo.",
            "20.jpg", 3),
        LibroSeed("La sombra del viento", "Carlos Ruiz Zafón", "Novela", "2001",
            "Daniel Sempere, un niño de diez años, es llevado por su padre al Cementerio de los Libros Olvidados, un lugar secreto donde los libros condenados al olvido esperan ser redescubiertos. Allí encuentra La sombra del viento, una novela de Julián Carax. Al investigar sobre el autor, Daniel descubre que alguien ha estado quemando todos los ejemplares de Carax y que su vida corre peligro. Entre misterios, amores prohibidos y la Barcelona de posguerra, Daniel deberá desentrañar el secreto que envuelve a Carax antes de que sea demasiado tarde.",
            "18.jpg", 2),
        LibroSeed("El alquimista", "Paulo Coelho", "Novela", "1988",
            "Santiago, un joven pastor andaluz, tiene un sueño recurrente sobre un tesoro escondido en las pirámides de Egipto. Vendiendo sus ovejas, emprende un viaje desde España hasta el desierto del Sahara, donde encuentra a un alquimista que le enseña a escuchar su corazón y a comprender el lenguaje del mundo. En su travesía descubre que el verdadero tesoro no es el oro, sino el conocimiento de sí mismo y la realización de su Leyenda Personal, un destino que cada persona debe cumplir para encontrar la felicidad.",
            "10.jpg", 5),
        LibroSeed("Don Quijote de la Mancha", "Miguel de Cervantes", "Clásico", "1605",
            "Alonso Quijano, un hidalgo manchego enloquecido por leer demasiados libros de caballerías, decide convertirse en caballero andante bajo el nombre de Don Quijote de la Mancha. Armado con una vieja armadura y montado en su rocín Rocinante, sale en busca de aventuras acompañado de su fiel escudero Sancho Panza. Juntos confunden molinos de viento con gigantes, ventas con castillos y rebaños de ovejas con ejércitos. La obra es una profunda reflexión sobre la realidad y la ficción, la locura y la cordura, y el poder transformador de los sueños.",
            "06.jpg", 2),
        LibroSeed("Orgullo y prejuicio", "Jane Austen", "Clásico", "1813",
            "En la Inglaterra rural del siglo XIX, la familia Bennet tiene cinco hijas solteras. La señora Bennet está empeñada en casarlas con hombres adinerados. La llegada del rico y orgulloso señor Darcy al vecindario despierta el interés de todos. Elizabeth Bennet, inteligente y de espíritu libre, choca inmediatamente con Darcy por su orgullo y arrogancia. Sin embargo, a medida que se conocen, ambos deben superar sus propios prejuicios para descubrir que el amor verdadero puede florecer incluso en las circunstancias más adversas.",
            "19.jpg", 4),
        LibroSeed("Crimen y castigo", "Fiódor Dostoyevski", "Clásico", "1866",
            "Rodión Raskólnikov, un joven estudiante en San Petersburgo, vive en la pobreza extrema. Convencido de que los hombres superiores tienen derecho a transgredir las leyes, asesina a una vieja usurera con un hacha. El crimen desencadena en él una profunda crisis psicológica: delirios de fiebre, paranoia y un sentimiento abrumador de culpa. Mientras lucha con su conciencia, conoce a Sonia, una joven obligada a prostituirse, cuyo amor y fe en Dios lo llevarán por el camino de la redención y la confesión.",
            "05.jpg", 3),
        LibroSeed("Matar a un ruiseñor", "Harper Lee", "Clásico", "1960",
            "En la pequeña ciudad sureña de Maycomb, Alabama, durante la Gran Depresión, el abogado Atticus Finch defiende a Tom Robinson, un hombre negro acusado injustamente de violar a una mujer blanca. A través de los ojos de su hija Scout, la novela explora el racismo, la injusticia y la pérdida de la inocencia. Atticus enseña a sus hijos que el verdadero valor no está en ganar, sino en defender lo correcto aunque sepas que vas a perder. Una obra que denuncia la discriminación racial con una sensibilidad y una humanidad inolvidables.",
            "08.jpg", 2),
        LibroSeed("1984", "George Orwell", "Distopía", "1949",
            "En Oceanía, un estado totalitario gobernado por el Partido Único y vigilado por el Gran Hermano, Winston Smith trabaja en el Ministerio de la Verdad alterando registros históricos para que coincidan con la propaganda oficial. Cansado de la represión absoluta, inicia una relación secreta con Julia y juntos intentan rebelarse contra el sistema. Descubren que incluso el pensamiento independiente es un crimen: el Pensamiento Delito. Atrapados por la Policía del Pensamiento, son sometidos a tortura física y psicológica hasta que Winston aprende la lección definitiva: dos más dos son cinco si el Partido así lo decide.",
            "12.jpg", 5),
        LibroSeed("Un mundo feliz", "Aldous Huxley", "Distopía", "1932",
            "En el año 2540, la humanidad vive en un Estado Mundial donde los seres humanos son producidos en masa en laboratorios, condicionados genéticamente y mentalmente para aceptar su casta social sin cuestionarla. El soma, una droga feliz, elimina cualquier sentimiento negativo. Bernard Marx, un hombre de casta alta con complejo de inferioridad, lleva a una salvaje del Reserva a la civilización para demostrar las fallas del sistema. Pero es el Salvaje, John, quien representa el choque entre los valores humanos tradicionales y la comodidad artificial de un mundo sin dolor, sin amor y sin libertad.",
            "03.jpg", 3),
        LibroSeed("Fahrenheit 451", "Ray Bradbury", "Distopía", "1953",
            "Guy Montag es un bombero, pero en su distópico futuro los bomberos no apagan incendios: los provocan para quemar libros, considerados la fuente de todo conflicto y sufrimiento humano. Montag nunca cuestiona su trabajo hasta que conoce a Clarisse, una joven que ve belleza en el mundo, y a Faber, un profesor que le enseña el valor del conocimiento. Comienza a robar libros y a leerlos en secreto, convirtiéndose en un fugitivo perseguido por el Estado. En su huida se encuentra con una comunidad de nómadas que memorizan libros enteros para preservar la cultura humana de la extinción.",
            "02.jpg", 4),
        LibroSeed("El cuento de la criada", "Margaret Atwood", "Distopía", "1985",
            "En la República de Gilead, un régimen teocrático y totalitario que ha reemplazado a Estados Unidos, las mujeres han sido despojadas de todos sus derechos. Defred es una Criada, una mujer fértil asignada a los Comandantes para procrear en un mundo donde la contaminación ha dejado estéril a la mayoría. Su único valor es su útero. Atrapada en un sistema que la usa como objeto, Defred debe sobrevivir mientras recuerda su vida anterior y busca pequeñas formas de resistencia. Es una advertencia escalofriante sobre cómo el fanatismo religioso y el control reproductivo pueden desmantelar la libertad en nombre del orden.",
            "04.jpg", 2),
        LibroSeed("El código Da Vinci", "Dan Brown", "Misterio", "2003",
            "El profesor Robert Langdon, experto en simbología, es convocado al Museo del Louvre en París después del asesinato del curador Jacques Saunière. El cuerpo del curador está dispuesto como el Hombre de Vitruvio de Da Vinci, y junto a él hay un código secreto. Langdon y la criptóloga Sophie Neveu descubren pistas ocultas en las obras de Leonardo Da Vinci que revelan un secreto milenario: el Santo Grial no es una copa, sino el linaje de María Magdalena y Jesucristo. Perseguidos por un fanático religioso y por la policía francesa, deben descifrar el código antes de que el secreto se pierda para siempre.",
            "11.jpg", 5),
        LibroSeed("El nombre de la rosa", "Umberto Eco", "Misterio", "1980",
            "En una abadía benedictina del norte de Italia en el año 1327, el monje franciscano Guillermo de Baskerville y su aprendiz Adso llegan para asistir a una disputa teológica. Pero la abadía está sumida en el terror: siete monjes han muerto en circunstancias misteriosas. Guillermo, un hábil detective que recuerda a Sherlock Holmes, investiga los crímenes mientras descubre que todos giran en torno a un libro prohibido escondido en la biblioteca del monasterio, un laberinto de conocimiento y pecado. Entre herejías, inquisidores y la lucha por el poder eclesiástico, la verdad es más peligrosa que cualquier ficción.",
            "16.jpg", 3),
        LibroSeed("La chica del tren", "Paula Hawkins", "Misterio", "2015",
            "Rachel Watson toma el tren todos los días a Londres y fantasea con la vida de una pareja perfecta que ve desde la ventana: Jess y Jason. Un día, Rachel presencia algo impactante desde el tren y se convierte en parte de una investigación policial cuando Jess desaparece. Pero Rachel tiene problemas con el alcohol, su memoria es fragmentada y nadie confía en ella. Mientras intenta reconstruir lo que vio, descubre que nada es lo que parece y que los secretos de la pareja perfecta son más oscuros de lo que imaginaba. Una historia de mentiras, obsesión y traición que mantiene al lector en vilo hasta la última página.",
            "01.jpg", 2),
        LibroSeed("Los hombres que no amaban a las mujeres", "Stieg Larsson", "Misterio", "2005",
            "El periodista Mikael Blomkvist es contratado por el anciano magnate Henrik Vanger para investigar la desaparición de su sobrina Harriet ocurrida cuarenta años atrás en la isla familiar de Hedeby. Blomkvist se une a Lisbeth Salander, una antisocial y brillante hacker con un pasado traumático. Juntos descubren que la familia Vanger esconde secretos terribles: abusos, crímenes y una violencia oculta durante generaciones. Lisbeth, con su habilidad para la investigación digital y su fuerza inquebrantable, se convierte en la pieza clave para resolver el caso, mientras lucha contra sus propios demonios y un sistema que la ha fallado.",
            "17.jpg", 4),
        LibroSeed("El principito", "Antoine de Saint-Exupéry", "Infantil", "1943",
            "Un piloto perdido en el desierto del Sahara conoce a un niño extraordinario que dice venir del asteroide B-612. El Principito le cuenta sus viajes por otros asteroides donde ha conocido personajes singulares: un rey sin súbditos, un vanidoso que solo quiere admiración, un bebedor que bebe para olvidar que bebe, un farolero que enciende y apaga su farol cada minuto, y un geógrafo que nunca ha viajado. En la Tierra, el Principito aprende sobre el amor y la amistad a través de su encuentro con un zorro, que le enseña que lo esencial es invisible a los ojos, y que el tiempo dedicado a alguien es lo que lo hace único e irrepetible.",
            "14.jpg", 5),
        LibroSeed("Harry Potter y la piedra filosofal", "J.K. Rowling", "Infantil", "1997",
            "Harry Potter vive en un armario debajo de las escaleras en la casa de sus tíos, los Dursley, que lo tratan como a un sirviente. En su undécimo cumpleaños, comienza a recibir cartas misteriosas que finalmente lo llevan a descubrir la verdad: es un mago, hijo de padres asesinados por el temible Lord Voldemort. Ingresa al Colegio Hogwarts de Magia y Hechicería, donde hace amigos inseparables como Ron Weasley y Hermione Granger. Juntos descubren que alguien intenta robar la Piedra Filosofal, un objeto mágico que otorga la inmortalidad. Enfrentando trampas, trolls y un profesor poseído, Harry demuestra que el amor de su madre lo protege con un poder que Voldemort nunca podrá entender.",
            "13.jpg", 4),
        LibroSeed("Las crónicas de Narnia", "C.S. Lewis", "Infantil", "1950",
            "Durante la Segunda Guerra Mundial, cuatro hermanos —Peter, Susan, Edmund y Lucy Pevensie— son evacuados a la casa de un profesor en el campo. En la habitación de los huéspedes, Lucy descubre un armario que la transporta a Narnia, un mundo mágico donde los animales hablan y el invierno eterno reina bajo el dominio de la Bruja Blanca, quien ha condenado a Narnia a un invierno que nunca termina y a una Navidad que nunca llega. Los niños se unen al Gran León Aslan para liberar Narnia de la tiranía, enfrentando traiciones, batallas y sacrificios. La historia explora temas de redención, fe, valentía y la lucha entre el bien y el mal en un mundo de fantasía inolvidable.",
            "07.jpg", 3),
        LibroSeed("El hobbit", "J.R.R. Tolkien", "Infantil", "1937",
            "Bilbo Bolsón es un hobbit tranquilo y respetable que nunca ha buscado aventuras. Su vida pacífica en la Comarca se ve interrumpida cuando el mago Gandalf y una compañía de trece enanos llegan a su puerta para reclutarlo como saqueador en su misión de recuperar el tesoro perdido de los enanos, custodiado por el dragón Smaug en la Montaña Solitaria. En su viaje, Bilbo descubre un anillo de invisibilidad en las cavernas de Gollum, un objeto que cambiará su destino y el de toda la Tierra Media. Bilbo demuestra que incluso el hobbit más pequeño puede cambiar el curso del mundo con astucia, coraje y un poco de suerte.",
            "09.jpg", 5)
    )

    fun insertar(db: SQLiteDatabase, context: Context) {
        for (libro in libros) {
            db.execSQL(
                "INSERT INTO Libros(nombre_libro, autor_libro, categoria, ano_creado, sinopsis, ruta_imagen, stock) VALUES(?, ?, ?, ?, ?, ?, ?)",
                arrayOf<Any>(libro.nombre, libro.autor, libro.categoria, libro.anio, libro.sinopsis, libro.img, libro.stock)
            )
        }

        completarFavoritosYPrestamos(db)

        val dir = File(context.filesDir, "images")
        if (!dir.exists()) dir.mkdirs()

        for (i in 1..20) {
            val nombre = String.format("%02d.jpg", i)
            val archivoDestino = File(dir, nombre)
            if (!archivoDestino.exists()) {
                try {
                    context.assets.open("imagenes/$nombre").use { input ->
                        archivoDestino.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                } catch (_: Exception) {
                }
            }
        }
    }

    fun actualizarDatosExistentes(db: SQLiteDatabase) {
        libros.forEach { libro ->
            db.execSQL(
                "UPDATE Libros SET stock = ? WHERE nombre_libro = ? AND stock = 1",
                arrayOf<Any>(libro.stock, libro.nombre)
            )
        }
        completarFavoritosYPrestamos(db)
    }

    private fun completarFavoritosYPrestamos(db: SQLiteDatabase) {
        val usuarios = mutableListOf<Int>()
        db.rawQuery("SELECT id FROM Usuarios WHERE es_admin = 0 ORDER BY id", null).use { cursor ->
            while (cursor.moveToNext()) usuarios.add(cursor.getInt(0))
        }

        val librosDisponibles = mutableListOf<Int>()
        db.rawQuery("SELECT id FROM Libros ORDER BY id", null).use { cursor ->
            while (cursor.moveToNext()) librosDisponibles.add(cursor.getInt(0))
        }

        if (usuarios.isEmpty() || librosDisponibles.size < maxOf(3, usuarios.size)) return

        usuarios.forEach { usuarioId ->
            val aleatorio = Random(20260713 + usuarioId)
            val librosUsados = mutableSetOf<Int>()
            db.rawQuery(
                "SELECT libro_id FROM Prestamos WHERE usuario_id = ?",
                arrayOf(usuarioId.toString())
            ).use { cursor ->
                while (cursor.moveToNext()) librosUsados.add(cursor.getInt(0))
            }

            if (!tieneEstado(db, usuarioId, "Aceptada")) {
                val libroAceptado = librosDisponibles
                    .filter { it !in librosUsados && tieneStock(db, it) }
                    .shuffled(aleatorio)
                    .firstOrNull()
                if (libroAceptado != null) {
                    db.execSQL(
                        """INSERT INTO Prestamos(
                            usuario_id, libro_id, fecha_prestamo, fecha_devolucion, estado
                        ) VALUES(?, ?, date('now','localtime','-2 days'), date('now','localtime','+5 days'), 'Aceptada')""".trimIndent(),
                        arrayOf(usuarioId, libroAceptado)
                    )
                    librosUsados.add(libroAceptado)
                }
            }

            if (!tieneEstado(db, usuarioId, "Pendiente")) {
                val libroPendiente = librosDisponibles
                    .filter { it !in librosUsados }
                    .shuffled(aleatorio)
                    .firstOrNull()
                if (libroPendiente != null) {
                    db.execSQL(
                        """INSERT INTO Prestamos(usuario_id, libro_id, fecha_prestamo, estado)
                           VALUES(?, ?, date('now','localtime','-1 day'), 'Pendiente')""".trimIndent(),
                        arrayOf(usuarioId, libroPendiente)
                    )
                    librosUsados.add(libroPendiente)
                }
            }

            if (!tieneEstado(db, usuarioId, "Rechazado")) {
                val libroRechazado = librosDisponibles
                    .filter { it !in librosUsados }
                    .shuffled(aleatorio)
                    .firstOrNull()
                if (libroRechazado != null) {
                    db.execSQL(
                        """INSERT INTO Prestamos(usuario_id, libro_id, fecha_prestamo, estado)
                           VALUES(?, ?, date('now','localtime','-4 days'), 'Rechazado')""".trimIndent(),
                        arrayOf(usuarioId, libroRechazado)
                    )
                }
            }

            val favoritosActuales = mutableSetOf<Int>()
            db.rawQuery(
                "SELECT libro_id FROM Favoritos WHERE usuario_id = ?",
                arrayOf(usuarioId.toString())
            ).use { cursor ->
                while (cursor.moveToNext()) favoritosActuales.add(cursor.getInt(0))
            }

            val cantidadFavoritos = if (favoritosActuales.size >= 2) {
                favoritosActuales.size
            } else {
                aleatorio.nextInt(2, 5)
            }
            librosDisponibles
                .filter { it !in favoritosActuales }
                .shuffled(aleatorio)
                .take(cantidadFavoritos - favoritosActuales.size)
                .forEach { libroId ->
                db.execSQL(
                    "INSERT INTO Favoritos(usuario_id, libro_id) VALUES(?, ?)",
                    arrayOf(usuarioId, libroId)
                )
            }
        }
    }

    private fun tieneEstado(db: SQLiteDatabase, usuarioId: Int, estado: String): Boolean {
        db.rawQuery(
            "SELECT COUNT(*) FROM Prestamos WHERE usuario_id = ? AND estado = ?",
            arrayOf(usuarioId.toString(), estado)
        ).use { cursor ->
            return cursor.moveToFirst() && cursor.getInt(0) > 0
        }
    }

    private fun tieneStock(db: SQLiteDatabase, libroId: Int): Boolean {
        db.rawQuery(
            """SELECT l.stock - (SELECT COUNT(*) FROM Prestamos p
                WHERE p.libro_id = l.id AND p.estado = 'Aceptada')
                FROM Libros l WHERE l.id = ?""".trimIndent(),
            arrayOf(libroId.toString())
        ).use { cursor ->
            return cursor.moveToFirst() && cursor.getInt(0) > 0
        }
    }
}
