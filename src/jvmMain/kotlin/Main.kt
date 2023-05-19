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

    //usuarios y contraseñas de la BD
    val statement = conn.createStatement()
    val resultado = statement.executeQuery("SELECT * FROM USERS")
    val listaUser = mutableListOf<DatosUser>()
    while(resultado.next()){
        listaUser.add(DatosUser(resultado.getString("USUARIO"), resultado.getString("PASSWORD")))
    }

    //LISTA INTERNA Y FILE EN DONDE ALMACENAREMOS LOS LIBROS
    val listaLibros = mutableListOf<Libro>()
    val file = File("C:\\Users\\Usuario\\Desktop\\proyectoPorg\\libros.txt")

    //EXTRAEMOS DATOS SI LA LISTA YA CONTIENE LIBROS (AUNQUE YA CONTAMOS CON ESOS DATOS EN LA PROPIA BD)
    if (file.readText().isNotEmpty()) {
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
    }

    MaterialTheme {
        var titulo by remember { mutableStateOf("") }
        var calificacion by remember { mutableStateOf("") }
        var autor by remember { mutableStateOf("") }
        var fechaLanz by remember { mutableStateOf("") }
        var precio by remember { mutableStateOf("") }
        var user by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var paginas by remember { mutableStateOf(20) }

        Image(
            painter = painterResource("bibliotec.jpg"),
            contentDescription = "imagenBiblioteca",
            modifier = Modifier.fillMaxSize()
        )

        when (paginas) {
            //Caja 0 (Inicio)
            20 -> {
                Column {
                    Row {
                        Text("LOGIN", color = Color.Red, fontSize = 40.sp, textAlign = TextAlign.Center)
                    }
                    Row {
                        TextField(value = user, onValueChange = { user = it }, label = { Text(text = "User") })
                    }
                    Row {
                        TextField(value = password, onValueChange = { password = it }, label = { Text(text = "Password") })
                    }
                    Row {
                        Button(onClick = {
                            val userIN = DatosUser(user, password)
                            print(userIN)
                            //esto hay que refactorizarlo crack
                            if (userIN.d1 == listaUser[0].d1 && userIN.d2 == listaUser[0].d2 || userIN.d1 == listaUser[1].d1 && userIN.d2 == listaUser[1].d2)
                            {
                                paginas = 0
                            }
                            else{
                                paginas = 13
                            }
                        }) {
                            Text("Aceptar")
                        }
                    }
                }
            }
            13 -> {
                //Pagina de errores
                Column {
                    Row {
                        Text("usuario o contraseña incorrectos!")
                    }
                    Row{
                        Button(onClick = {
                            paginas = 20
                        }) {
                            Text("volver al login")
                            //estaria bien meter un contador de fallos para que cuando lleve 3 login erroneos salga de la app por ejemplo
                        }
                    }
                }
            }
            0 -> {
                Box {
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
                var texto = ""
                listaLibros.forEach { texto += "$it\n" }
                file.writeText(texto)
            }

            //Select de libros
            2 -> {
                Box {
                    LazyColumn {
                        item {
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
                                paginas = 0
                            }) {
                                Text("volver al inicio")
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
        }
    }
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
fun minLibro(libros: MutableList<Libro>): String{
    var cadena = ""
    val libro = libros.sortedBy { it.calif }[0]
    cadena += "Libro con mayor calificacion" + "\n" + "-".repeat(28) + "\n" +
            "| ${libro.titulo} | ${libro.calif} | ${libro.autor}| ${libro.fech_lanz}| ${libro.precio} |" + "\n" + "-".repeat(28)
    return cadena
}
//representar tabla mas bonita wei
@Composable
@Preview
fun maxLibro(libros: MutableList<Libro>): String{
    var cadena = ""
    val libro = libros.sortedBy { it.calif }[libros.size-1]
    cadena += "Libro con mayor calificacion" + "\n" + "-".repeat(28) + "\n" +
            "| ${libro.titulo} | ${libro.calif} | ${libro.autor}| ${libro.fech_lanz}| ${libro.precio} |" + "\n" + "-".repeat(28)
    return cadena
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        BookApp()
    }
}