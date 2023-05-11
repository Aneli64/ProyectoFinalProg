import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.io.File
import java.sql.DriverManager
@Composable
@Preview
fun BookApp() {
    val url = "jdbc:oracle:thin:@localhost:1521:xe"
    val usuario = "libros"
    val contraseña = "libros"
    val conn = DriverManager.getConnection(url, usuario, contraseña)

    val listaLibros = mutableListOf<Libro>()
    val file = File("C:\\Users\\Usuario\\Desktop\\proyectoPorg\\libros.txt")

    //listaLibros.forEach { file.appendText("$it\n") } y al reves

    MaterialTheme {
        var titulo by remember { mutableStateOf("") }
        var calificacion by remember { mutableStateOf("") }
        var autor by remember { mutableStateOf("") }
        var fechaLanz by remember { mutableStateOf("") }
        var precio by remember { mutableStateOf("") }


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
                    //faltaria guardarlo en un file



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
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        BookApp()
    }
}