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

@Composable
@Preview
fun AppLibros() {
    val listaLibros = mutableListOf<Libro>()

    MaterialTheme {
        var titulo by remember { mutableStateOf("") }
        var calif by remember { mutableStateOf("") }
        var autor by remember { mutableStateOf("") }
        var fech_lanz by remember { mutableStateOf("") }
        var precio by remember { mutableStateOf("") }


        Column {
            TextField(value = titulo,
                onValueChange = { titulo = it },
                label = { Text(text = "Titulo") }
            )

            TextField(value = calif,
                onValueChange = { calif = it },
                label = { Text(text = "Calificacion") }
            )

            TextField(value = fech_lanz,
                onValueChange = { fech_lanz = it },
                label = { Text(text = "Fecha de lanzamiento") }
            )

            TextField(value = precio,
                onValueChange = { precio = it },
                label = { Text(text = "Precio") }
            )

            Row {
                Button(onClick = {
                    val libro = Libro(titulo, calif.toDouble(), autor, fech_lanz, precio.toDouble())
                    listaLibros.add(libro)

                    titulo = ""
                    calif = ""
                    autor = ""
                    fech_lanz = ""
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
        AppLibros()
    }
}