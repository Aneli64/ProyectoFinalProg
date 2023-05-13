import androidx.compose.desktop.ui.tooling.preview.Preview
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

    //EXTRAEMOS DATOS SI LA LISTA YA CONTIENE LIBROS (AUNQUE YA CONTAMOS CON ESOS DATOS EN LA PROPIA BD)
    if (file.readText().isNotEmpty()) {
        file.forEachLine {
            listaLibros.add(
                Libro(
                    it.split(",")[0], it.split(",")[1].toDouble(), it.split(",")[2],
                    it.split(",")[3], it.split(",")[4].toDouble()
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
        var paginas by remember { mutableStateOf(0) }

        when (paginas) {
            //Caja 0 (Inicio)
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
                    }
                }

            }
            //Caja 1 (Insercción de libros)
            1 -> {
                Box {
                    Column {
                        TextField(value = titulo,
                            onValueChange = { titulo = it },
                            label = { Text(text = "Titulo") }
                        )

                        TextField(value = calificacion,
                            onValueChange = { calificacion = it },
                            label = { Text(text = "Calificacion") }
                        )

                        TextField(value = autor,
                            onValueChange = { autor = it },
                            label = { Text(text = "Autor") }
                        )

                        TextField(value = fechaLanz,
                            onValueChange = { fechaLanz = it },
                            label = { Text(text = "Fecha de lanzamiento") }
                        )

                        TextField(value = precio,
                            onValueChange = { precio = it },
                            label = { Text(text = "Precio") }
                        )

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
                                println(listaLibros.toList())
                            }) {
                                Text("Show Books")
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
                var text = "" //sigamos con esto, pero hay que hacer una tabla para que se vea gucci
                listaLibros.forEach { text += "$it\n" }
                Text(text)
                /*val regist = conn.createStatement()
                val query = "SELECT * FROM BIBLIOTECA"
                val select = regist.executeQuery(query)*/
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        BookApp()
    }
}