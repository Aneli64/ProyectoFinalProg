import java.sql.*

fun main() {

    val url = "jdbc:oracle:thin:@localhost:1521:xe"
    val usuario = "libros"
    val contraseña = "libros"

    val valor1 = Libro("Libro1", 7.5, "Pepito", "02/05/2008", 4.5)

    val conn = DriverManager.getConnection(url, usuario, contraseña)
    val insert = conn.prepareStatement("INSERT INTO Biblioteca (titulo, calificacion, autor, fechaLanz, precio) VALUES (?, ?, ?, ?, ?)")

    insert.setString(1, valor1.titulo)
    insert.setDouble(2, valor1.calif)
    insert.setString(3, valor1.autor)
    insert.setString(4, valor1.fech_lanz)
    insert.setDouble(5, valor1.precio)

    insert.executeUpdate()
    //println("Inserción exitosa")

    val statement = conn.createStatement()

    val query = "SELECT titulo, calificacion FROM Biblioteca"
    val resultSet = statement.executeQuery(query)
}