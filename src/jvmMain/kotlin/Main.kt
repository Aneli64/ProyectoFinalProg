import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontFamily
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
    //CONEXION A BD
    val url = "jdbc:oracle:thin:@localhost:1521:xe"
    val usuario = "libros"
    val contraseña = "libros"
    val conexBD = DriverManager.getConnection(url, usuario, contraseña)

    //Statment base de la BD
    val statement = conexBD.createStatement()

    //LISTA INTERNA Y FILE EN DONDE ALMACENAREMOS LOS LIBROS
    val listaLibros = mutableListOf<Libro>()
    val fileLibros = File("C:\\Users\\Usuario\\Desktop\\proyectoPorg\\libros.txt") //DEFINIR RUTA ABSOLUTA PLS

    //usuarios y contraseñas de la BD
    val select = statement.executeQuery("SELECT * FROM USERS")
    val mapaUsers = mutableMapOf<String, String>()
    while (select.next()) {
        mapaUsers[select.getString("USUARIO")] = select.getString("PASSWORD")
    }

    //SELECT DE LIBROS (PARA SU USO EN SELECT)
    val statmentAddFileLista = conexBD.createStatement()
    val addToFileList = statement.executeQuery("SELECT * FROM Biblioteca")

    //EXTRAEMOS LIBROS DE LA BD Y LOS AÑADIMOS A LA LISTA INTERNA Y AL FILE
    while (addToFileList.next()) {
        val libroIn = Libro(
            addToFileList.getString("TITULO"), addToFileList.getDouble("CALIFICACION"),
            addToFileList.getString("AUTOR"),
            addToFileList.getString("FECHALANZ"),
            addToFileList.getDouble("PRECIO")
        )
        listaLibros.add(libroIn)
    }
    addTextFile(listaLibros, fileLibros)

    //STATMENT DE LIBROS (PARA SU USO EN UPDATE)
    val statmentUpdate = conexBD.createStatement()

    //STATMENT DE LIBROS (PARA SU USO EN DELETE)
    val statmentDelete = conexBD.createStatement()

    //LISTA DE VARIABLES QUE USAREMOS EN NUESTRO PROGRAMA
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

        //variables para su uso en la pagina delete
        var libroEncontradoDelete by remember { mutableStateOf(false) }
        var libroDelete by remember { mutableStateOf("") }

        //variables de login
        var user by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var adminLogin by remember { mutableStateOf(false) }

        //variable que nos indica la pagina en la que nos encontramos
        var paginas by remember { mutableStateOf(20) }

        //GUIA DE PAGINAS
        /*
        - 20,21 -> PAGINAS DE LOGIN
        - 13 -> PAGINA DE ERRORES
        - 0 -> PAGINA DE INICIO
        - 1 -> INSERT DE LIBROS
        - 2 -> SELECT DE LIBROS
        - 3,4,5 -> INFORMACION VARIA SOBRE LIBROS (MAYOR, MENOR CALIFICACION)
        - 6,7 -> ALTERAR LIBROS
        - 8 -> DELETE DE LIBROS
         */

        when (paginas) {
            20 -> {
                //LOGIN (usuarios normales)

                //IMAGEN DE LOGIN
                Image(
                    painter = painterResource("loginFondo.png"),
                    contentDescription = "login",
                    modifier = Modifier.fillMaxSize().fillMaxHeight()
                )
                Column(
                    Modifier.padding(200.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row {
                        Text(
                            "LOGIN",
                            color = Color.Red,
                            fontSize = 40.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace
                        )

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
                        }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
                            Text("Aceptar")
                        }
                    }
                }
            }

            21 -> {
                //ADMIN LOGIN

                //IMAGEN DE LOGIN
                Image(
                    painter = painterResource("loginFondo.png"),
                    contentDescription = "login",
                    modifier = Modifier.fillMaxSize().fillMaxHeight()
                )
                Column {
                    Row {
                        Text(
                            "ADMIN LOGIN",
                            color = Color.Red,
                            fontSize = 40.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace
                        )
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
                        paginas = if (userADMIN.d1 == "admin" && userADMIN.d2 == "admin") 6 else 13
                    }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
                        Text("Aceptar")
                    }
                }
            }

            13 -> {
                //Pagina de errores
                Column {
                    Row {
                        Text(
                            "usuario o contraseña incorrectos!",
                            color = Color.Red,
                            fontSize = 40.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Row {
                        Button(onClick = {
                            paginas = if (adminLogin) 21 else 20
                        }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
                            Text("volver al login")
                        }
                    }
                }
            }

            0 -> {
                //Caja 0 (Inicio)

                //IMAGEN EN LA APP
                Image(
                    painter = painterResource("loginBib.jpg"),
                    contentDescription = "login",
                    modifier = Modifier.fillMaxSize().fillMaxHeight()
                )

                Box {
                    //definimos adminLogin a True para que al fallar en admin Login nos devuelva a su respectivo login de error
                    adminLogin = true
                    Column {
                        Row {
                            Text(
                                "BIBLIOTECA",
                                color = Color.Red,
                                fontSize = 40.sp,
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Row {
                            Button(onClick = {
                                paginas = 1
                            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
                                Text("Ir a inserc")
                            }
                        }
                        Row {
                            Button(onClick = {
                                paginas = 2
                            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
                                Text("Ir a select")
                            }
                        }
                        Row {
                            Button(onClick = {
                                paginas = 3
                            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
                                Text("Inf libros")
                            }
                        }
                        Row {
                            Button(onClick = {
                                paginas = 21
                            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
                                Text("Alterar datos de los libros")
                            }
                        }
                        Row {
                            Button(onClick = {
                                paginas = 8
                            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
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
                                val insertar =
                                    conexBD.prepareStatement("INSERT INTO Biblioteca (titulo, calificacion, autor, fechaLanz, precio) VALUES (?, ?, ?, ?, ?)")
                                insertar.setString(1, libro.titulo)
                                insertar.setDouble(2, libro.calif)
                                insertar.setString(3, libro.autor)
                                insertar.setString(4, libro.fech_lanz)
                                insertar.setDouble(5, libro.precio)
                                insertar.executeUpdate()

                                //una vez guardado, dejamos vacíos sus campos para introducir uno nuevo
                                titulo = ""
                                calificacion = ""
                                autor = ""
                                fechaLanz = ""
                                precio = ""

                            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
                                Text("Add Book")
                            }
                            Button(onClick = {
                                paginas = 0
                            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
                                Text("volver al inicio")
                            }
                        }
                    }
                }
                //y lo añadimos a nuestro file para poder manipularlo mas adelante
                addTextFile(listaLibros, fileLibros)
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
                            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
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
                            Text(
                                "Informacion varia acerca de los libros",
                                color = Color.Red,
                                fontSize = 40.sp,
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Row {
                            Button(onClick = {
                                paginas = 4
                            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
                                Text("Libro con mayor calif")
                            }
                        }
                        Row {
                            Button(onClick = {
                                paginas = 5
                            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
                                Text("Libro con menor calif")
                            }
                        }
                        Row {
                            Button(onClick = {
                                paginas = 0
                            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
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
                        }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
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
                        }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
                            Text("volver al inicio")
                        }
                    }
                }
            }

            6 -> {
                Column {
                    Row {
                        Text(
                            "Alterar Datos",
                            color = Color.Red,
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace
                        )
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
                            if (libroEncontrado) paginas = 7
                        }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
                            Text("Alterar sus datos")
                        }
                    }
                }
            }

            7 -> {
                Column {
                    Row {
                        Text(
                            "Introduzca el campo que desea alterar y su nuevo dato",
                            color = Color.Red,
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace
                        )
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
                        Button(onClick = {
                            when (atributoIN) {
                                "titulo" -> statmentUpdate.executeQuery("Update Biblioteca Set TITULO='$nuevoDatoUpdate' Where TITULO = '$tituloUpdate'")
                                "calificacion" -> statmentUpdate.executeQuery("Update Biblioteca Set CALIFICACION='$nuevoDatoUpdate' Where TITULO = '$tituloUpdate'")
                                "autor" -> statmentUpdate.executeQuery("Update Biblioteca Set AUTOR='$nuevoDatoUpdate' Where TITULO = '$tituloUpdate'")
                                "fechaLanz" -> statmentUpdate.executeQuery("Update Biblioteca Set FECHALANZ='$nuevoDatoUpdate' Where TITULO = '$tituloUpdate'")
                                "precio" -> statmentUpdate.executeQuery("Update Biblioteca Set PRECIO='$nuevoDatoUpdate' Where TITULO = '$tituloUpdate'")
                            }

                            val update = statement.executeQuery("SELECT * FROM Biblioteca")
                            //aplicamos los cambios a la lista interna
                            listaLibros.clear()
                            while (update.next()) {
                                val libroIn = Libro(
                                    update.getString("TITULO"), update.getDouble("CALIFICACION"),
                                    update.getString("AUTOR"),
                                    update.getString("FECHALANZ"),
                                    update.getDouble("PRECIO")
                                )
                                listaLibros.add(libroIn)
                            }

                            //aplicamos los cambios a nuestro file de libros almacenados
                            addTextFile(listaLibros, fileLibros)

                        }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
                            Text("Update")
                        }
                    }
                    Row {
                        Button(onClick = {
                            paginas = 0
                        }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
                            Text("volver al inicio")
                        }
                    }
                }
            }

            8 -> {
                Column {
                    Row {
                        Text(
                            "BORRAR LIBROS",
                            color = Color.Red,
                            fontSize = 40.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Row {
                        TextField(
                            value = libroDelete,
                            onValueChange = { libroDelete = it },
                            label = { Text(text = "Nombre del libro") })
                    }
                    Row {
                        //consulta de la bd para su uso en .next() y borrado
                        val delete = statement.executeQuery("SELECT * FROM Biblioteca")
                        Button(onClick = {
                            for (item in listaLibros) if (item.titulo == libroDelete) {
                                libroEncontradoDelete = true
                            }
                            if (libroEncontradoDelete) {
                                val query = "DELETE FROM Biblioteca Where TITULO = '$libroDelete'"
                                while (delete.next()) {
                                    statmentDelete.executeQuery(query)
                                }
                                val borrado = conexBD.prepareStatement(query)
                                borrado.executeUpdate()
                                listaLibros.removeIf { it.titulo == libroDelete }

                                //añadimos cambios de nuevo al file
                                addTextFile(listaLibros, fileLibros)

                            }
                        }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
                            Text("Borrar libro")
                        }
                    }
                    Row {
                        Button(onClick = {
                            paginas = 0
                        }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)) {
                            Text("volver al inicio")
                        }
                    }
                }
            }
        }
    }
}

//Funcion que utilizaremos para añadir rapidamente la lista de libros existentes con sus recientes cambios a nuestro File
fun addTextFile(listaLibros: MutableList<Libro>, file: File) {
    var textIN = ""
    listaLibros.forEach { textIN += "$it\n" }
    file.writeText(textIN)
}

//Funcion que utilizaremos para representar nuestros libros en una tabla
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

//Funcion que nos muestra el libro con la minima calificacion
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

//Funcion que nos muestra el libro con la maxima calificacion
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