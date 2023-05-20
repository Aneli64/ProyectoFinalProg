import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.io.File
import java.sql.DriverManager

@Composable
@Preview
fun BookApp() {
    //CONEXION A BD BIBLIOTECA
    val url = "jdbc:oracle:thin:@localhost:1521:xe"
    val usuario = "libros"
    val contraseña = "libros"
    val conn = DriverManager.getConnection(url, usuario, contraseña)

    //LISTA INTERNA Y FILE EN DONDE ALMACENAREMOS LOS LIBROS
    val listaLibros = mutableListOf<Libro>()
    val file = File("C:\\Users\\Usuario\\Desktop\\proyectoPorg\\libros.txt")

    //usuarios y contraseñas de la BD
    val statement = conn.createStatement()
    val resultado = statement.executeQuery("SELECT * FROM USERS")
    val mapaUsers = mutableMapOf<String, String>()
    while (resultado.next()) {
        mapaUsers[resultado.getString("USUARIO")] = resultado.getString("PASSWORD")
    }

    //SELECT DE LIBROS (PARA SU USO EN SELECT, UPDATE Y DELETE)
    val operac = conn.createStatement()
    val selectBibl = statement.executeQuery("SELECT * FROM Biblioteca")

    //EXTRAEMOS DATOS SI LA LISTA YA CONTIENE LIBROS (AUNQUE YA CONTAMOS CON ESOS DATOS EN LA PROPIA BD)
    while (selectBibl.next()) {
        val libroIn = Libro(
            selectBibl.getString("TITULO"), selectBibl.getDouble("CALIFICACION"),
            selectBibl.getString("AUTOR"),
            selectBibl.getString("FECHALANZ"),
            selectBibl.getDouble("PRECIO")
        )
        listaLibros.add(libroIn)
    }
    addTextFile(listaLibros, file)
    /*if (file.readText().isNotEmpty()) {
        file.forEachLine {
            listaLibros.add(
                Libro(
                    it.split(",")[0],
                    it.split(",")[1].toDouble(),
                    it.split(",")[2],
                    it.split(",")[3],
                    it.split(",")[4].toDouble()
                )
            )
        }
    }*/

    MaterialTheme {
        //variables de atributos sobre libros a insertar
        var titulo by remember { mutableStateOf("") }
        var calificacion by remember { mutableStateOf("") }
        var autor by remember { mutableStateOf("") }
        var fechaLanz by remember { mutableStateOf("") }
        var precio by remember { mutableStateOf("") }

        //variables para su uso en la pagina update
        var libroEncontrado by remember { mutableStateOf(false) }
        var tituloUpdate by remember { mutableStateOf("") }
        var atributoIN by remember { mutableStateOf("") }
        var nuevoDatoUpdate by remember { mutableStateOf("") }
        //mirar si estos dos grupos de variables se pueden unir para ahorrar codigo

        //variables para su uso en la pagina delete
        var libroEncontradoDelete by remember { mutableStateOf(false) }
        var libroDelete by remember { mutableStateOf("") }

        //variables de login
        var user by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var adminLogin by remember { mutableStateOf(false) }

        //variable que nos indica la pagina en la que nos encontramos
        var paginas by remember { mutableStateOf(20) }
        //ESTARIA BIEN HACER UNA GUIA SOBRE LAS PAGINAS QUE LLEVAN A CADA COSA

        //IMAGENES DE FONDO DE NUESTRA APP

        //IMAGEN DE INICIO
        Image(
            painter = painterResource("bibliotec.jpg"),
            contentDescription = "imagenBiblioteca",
            modifier = Modifier.fillMaxSize()
        )

        when (paginas) {
            //LOGIN (usuarios normales)
            20 -> {

                Column {
                    Row {
                        Text("LOGIN", color = Color.Red, fontSize = 40.sp, textAlign = TextAlign.Center)
                    }
                    Row {
                        TextField(value = user, onValueChange = { user = it }, label = { Text(text = "User") })
                    }
                    Row {
                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(text = "Password") })
                    }
                    Row {
                        //extraemos los usuarios y contraseñas que tienen login en nuestra app
                        //y lo almacenamos en un mapa para comprobar que nuestro login se encuentra en nuestra BD
                        Button(onClick = {
                            val userIN = DatosUser(user, password)
                            paginas = if (mapaUsers[userIN.d1] == userIN.d2) 0 else 13
                        }) {
                            Text("Aceptar")
                        }
                    }
                }
            }

            21 -> {
                //ADMIN LOGIN
                //ESTARIA BIEN PONER QUE SI YA ACCEDE COMO ADMIN DE PRIMERAS, NO PIDA LUEGO SU LOGIN DE ADMIN
                Column {
                    Row {
                        Text("ADMIN LOGIN", color = Color.Red, fontSize = 40.sp, textAlign = TextAlign.Center)
                    }
                    Row {
                        TextField(value = user, onValueChange = { user = it }, label = { Text(text = "User") })
                    }
                    Row {
                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(text = "Password") })
                    }
                    Button(onClick = {
                        val userADMIN = DatosUser(user, password)
                        paginas = if (userADMIN.d1 == "admin" && userADMIN.d2 == "admin") 7 else 13
                    }) {
                        Text("Aceptar")
                    }
                }
            }

            13 -> {
                //Pagina de errores
                Column {
                    Row {
                        Text("usuario o contraseña incorrectos!")
                    }
                    Row {
                        Button(onClick = {
                            paginas = if (adminLogin) 21 else 20
                        }) {
                            Text("volver al login")
                        }
                    }
                }
            }

            0 -> {
                //Caja 0 (Inicio)
                Box {
                    //definimos adminLogin a True para que al fallar en admin Login nos devuelva a su respectivo login de error
                    adminLogin = true
                    Column {
                        Row {
                            Text("BIBLIOTECA", color = Color.Blue, fontSize = 40.sp, textAlign = TextAlign.Center)
                        }
                        Row {
                            Button(onClick = {
                                paginas = 1
                            }) {
                                Text("Ir a inserc")
                            }
                        }
                        Row {
                            Button(onClick = {
                                paginas = 2
                            }) {
                                Text("Ir a select")
                            }
                        }
                        Row {
                            Button(onClick = {
                                paginas = 3
                            }) {
                                Text("Inf libros")
                            }
                        }
                        Row {
                            Button(onClick = {
                                paginas = 6
                            }) {
                                Text("Alterar datos de los libros")
                            }
                        }
                        Row {
                            Button(onClick = {
                                paginas = 9
                            }) {
                                Text("Borrar libros")
                            }
                        }
                    }
                }

            }
            //Caja 1 (Insercción de libros)
            1 -> {
                Box {
                    Column {
                        TextField(value = titulo, onValueChange = { titulo = it }, label = { Text(text = "Titulo") })

                        TextField(value = calificacion,
                            onValueChange = { calificacion = it },
                            label = { Text(text = "Calificacion") })

                        TextField(value = autor, onValueChange = { autor = it }, label = { Text(text = "Autor") })

                        TextField(value = fechaLanz,
                            onValueChange = { fechaLanz = it },
                            label = { Text(text = "Fecha de lanzamiento") })

                        TextField(value = precio, onValueChange = { precio = it }, label = { Text(text = "Precio") })

                        Row {
                            Button(onClick = {
                                //creamos y almacenamos el libro en la lista libros
                                val libro = Libro(titulo, calificacion.toDouble(), autor, fechaLanz, precio.toDouble())
                                listaLibros.add(libro)

                                //creamos la sentencia insert unido a una conexion previamente creada para guardar el libro en la base de datos
                                val insert =
                                    conn.prepareStatement("INSERT INTO Biblioteca (titulo, calificacion, autor, fechaLanz, precio) VALUES (?, ?, ?, ?, ?)")
                                insert.setString(1, libro.titulo)
                                insert.setDouble(2, libro.calif)
                                insert.setString(3, libro.autor)
                                insert.setString(4, libro.fech_lanz)
                                insert.setDouble(5, libro.precio)
                                insert.executeUpdate()

                                //una vez guardado, dejamos vacíos sus campos para introducir uno nuevo
                                titulo = ""
                                calificacion = ""
                                autor = ""
                                fechaLanz = ""
                                precio = ""

                            }) {
                                Text("Add Book")
                            }
                            Button(onClick = {
                                paginas = 0
                            }) {
                                Text("volver al inicio")
                            }
                        }
                    }
                }
                //y lo añadimos a nuestro file para poder manipularlo mas adelante
                addTextFile(listaLibros, file)
            }

            //Libros que se encuentran almacenados
            2 -> {
                Box {
                    Column {
                            Row {
                                Text(tabla(listaLibros))
                            }
                            Row {
                                Button(onClick = {
                                    paginas = 0
                                }) {
                                    Text("volver al inicio")
                                }
                            }
                    }
                }
            }

            3 -> {
                Box {
                    Column {
                        Row {
                            Text("Informacion varia acerca de los libros")
                        }
                        Row {
                            Button(onClick = {
                                paginas = 4
                            }) {
                                Text("Libro con mayor calif")
                            }
                        }
                        Row {
                            Button(onClick = {
                                paginas = 5
                            }) {
                                Text("Libro con menor calif")
                            }
                        }
                        Row {
                            Button(onClick = {
                                paginas = 3
                            }) {
                                Text("Atras")
                            }
                        }
                    }
                }
            }

            4 -> {
                Column {
                    Row {
                        Text(maxLibro(listaLibros))
                    }
                    Row {
                        Button(onClick = {
                            paginas = 0
                        }) {
                            Text("volver al inicio")
                        }
                    }
                }
            }

            5 -> {
                Column {
                    Row {
                        Text(minLibro(listaLibros))
                    }
                    Row {
                        Button(onClick = {
                            paginas = 0
                        }) {
                            Text("volver al inicio")
                        }
                    }
                }
            }

            6 -> {
                Column {
                    Row {
                        Text("Introduzca el nombre de algun libro para alterar sus datos")
                    }
                    Row {
                        Button(onClick = {
                            paginas = 21
                        }) {
                            Text("Alterar datos")
                        }
                    }
                    Row {
                        Button(onClick = {
                            paginas = 0
                        }) {
                            Text("volver al inicio")
                        }
                    }
                }
            }

            7 -> {
                Column {
                    Row {
                        Text("ALTERAR DATOS")
                    }
                    Row {
                        TextField(
                            value = titulo,
                            onValueChange = { titulo = it },
                            label = { Text(text = "Titulo") })
                    }
                    Row {
                        Button(onClick = {
                            for (item in listaLibros) if (item.titulo == titulo) {
                                libroEncontrado = true
                                tituloUpdate = item.titulo
                            }
                            if (libroEncontrado) paginas = 8
                        }) {
                            Text("Alterar sus datos")
                        }
                    }
                }
            }

            8 -> {
                Column {
                    Row {
                        Text("Introduzca el campo que desea alterar y su nuevo dato")
                    }
                    Row {
                        TextField(
                            value = atributoIN,
                            onValueChange = { atributoIN = it },
                            label = { Text(text = "Campo a alterar (titulo, calificacion, autor, fechaLanz, precio)") })
                    }
                    Row {
                        TextField(
                            value = nuevoDatoUpdate,
                            onValueChange = { nuevoDatoUpdate = it },
                            label = { Text(text = "Nuevo dato") })
                    }
                    Row {
                        Button(onClick = { //corregir que calif y precio solo se pueda insertar con coma para decimales!!!
                            when (atributoIN) { //esto se podria refactorizar verdad figura??
                                "titulo" -> operac.executeQuery("Update Biblioteca Set TITULO='$nuevoDatoUpdate' Where TITULO = '$tituloUpdate'")
                                "calificacion" -> operac.executeQuery("Update Biblioteca Set CALIFICACION='$nuevoDatoUpdate' Where TITULO = '$tituloUpdate'")
                                "autor" -> operac.executeQuery("Update Biblioteca Set AUTOR='$nuevoDatoUpdate' Where TITULO = '$tituloUpdate'")
                                "fechaLanz" -> operac.executeQuery("Update Biblioteca Set FECHALANZ='$nuevoDatoUpdate' Where TITULO = '$tituloUpdate'")
                                "precio" -> operac.executeQuery("Update Biblioteca Set PRECIO='$nuevoDatoUpdate' Where TITULO = '$tituloUpdate'")
                            }

                            //aplicamos los cambios a la lista interna
                            listaLibros.clear()
                            while (selectBibl.next()) {
                                val libroIn = Libro(
                                    selectBibl.getString("TITULO"), selectBibl.getDouble("CALIFICACION"),
                                    selectBibl.getString("AUTOR"),
                                    selectBibl.getString("FECHALANZ"),
                                    selectBibl.getDouble("PRECIO")
                                )
                                listaLibros.add(libroIn)
                            }

                            //y tambien a nuestro file de libros almacenados
                            addTextFile(listaLibros, file)

                        }) {
                            Text("Update")
                        }
                    }
                    Row {
                        Button(onClick = {
                            paginas = 0
                        }) {
                            Text("volver al inicio")
                        }
                    }
                }
            }
            9 -> {
                Column {
                    Row {
                        Text("BORRAR LIBROS")
                    }
                    Row {
                        TextField(
                            value = libroDelete,
                            onValueChange = { libroDelete = it },
                            label = { Text(text = "Nombre del libro") })
                    }
                    Row {
                        Button(onClick = {
                            for (item in listaLibros) if (item.titulo == libroDelete) {
                                libroEncontradoDelete = true
                                libroDelete = item.titulo
                            }
                            if (libroEncontradoDelete) {
                                while(selectBibl.next()){
                                    operac.executeQuery("DELETE FROM Biblioteca Where TITULO = '$libroDelete'")
                                }
                                listaLibros.removeIf { it.titulo == libroDelete }

                                //añadimos cambios de nuevo al file
                                addTextFile(listaLibros, file)

                            }
                        }) {
                            Text("Borrar libro")
                        }
                    }
                    Row {
                        Button(onClick = {
                            paginas = 0
                        }) {
                            Text("volver al inicio")
                        }
                    }
                }
            }
        }
    }
}
fun addTextFile(listaLibros: MutableList<Libro>, file: File) {
    var textIN = ""
    listaLibros.forEach { textIN += "$it\n" }
    file.writeText(textIN)
}
@Composable
@Preview
fun tabla(lista: MutableList<Libro>): String { //intentar que quede mas bonita
    var texto = ""
    val cadena1 = "-".repeat(40)
    lista.forEach {
        Column {
            texto += cadena1 + "\n"
            texto += "| ${it.titulo} | ${it.calif} | ${it.autor}| ${it.fech_lanz}| ${it.precio} |" + "\n"
            texto += cadena1
        }
    }
    return texto
}

@Composable
@Preview
fun minLibro(libros: MutableList<Libro>): String {
    var cadena = ""
    val libro = libros.sortedBy { it.calif }[0]
    cadena += "Libro con mayor calificacion" + "\n" + "-".repeat(28) + "\n" +
            "| ${libro.titulo} | ${libro.calif} | ${libro.autor}| ${libro.fech_lanz}| ${libro.precio} |" + "\n" + "-".repeat(
        28
    )
    return cadena
}

@Composable
@Preview
fun maxLibro(libros: MutableList<Libro>): String {
    var cadena = ""
    val libro = libros.sortedBy { it.calif }[libros.size - 1]
    cadena += "Libro con mayor calificacion" + "\n" + "-".repeat(28) + "\n" +
            "| ${libro.titulo} | ${libro.calif} | ${libro.autor}| ${libro.fech_lanz}| ${libro.precio} |" + "\n" + "-".repeat(
        28
    )
    return cadena
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        BookApp()
    }
}