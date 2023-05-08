import java.sql.*

fun main() {

    val url = "jdbc:oracle:thin:@localhost:1521:xe"
    val usuario = "usuario"
    val contraseña = "usuario"
    /*try {
        Class.forName("oracle.jdbc.driver.OracleDriver")
        val conexion = DriverManager.getConnection(url, usuario, contraseña)
        println("Conexión exitosa")
        conexion.close()
    } catch (e: SQLException) {
        println("Error en la conexión: ${e.message}")
    } catch (e: ClassNotFoundException) {
        println("No se encontró el driver JDBC: ${e.message}")
    }*/

    data class TablaLibros(
        val column1: String, val column2: Double, val column3: String, val column4: String,
        val column5: Double
    )

    /*class Libro(
        private var titulo: String, private var calif: Double, private var autor: String, private var fech_lanz: String, private var precio: Double)*/

    //Class.forName("oracle.jdbc.driver.OracleDriver")
    val conexion = DriverManager.getConnection(url, usuario, contraseña)
    val valor1 = TablaLibros("Libro1", 7.5, "Pepito", "02/05/2008", 14.5)

    val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydatabase", "usuario", "usuario")
    val insert = conn.prepareStatement("INSERT INTO TablaLibros (column1, column2, column3, column4, column5) VALUES (?, ?, ?, ?, ?)")

    insert.setString(1, valor1.column1)
    insert.setDouble(2, valor1.column2)
    insert.setString(3, valor1.column3)
    insert.setString(4, valor1.column4)
    insert.setDouble(5, valor1.column5)

    insert.executeUpdate()
    println("Inserción exitosa")

    val statement = conn.createStatement()


    val query = "SELECT id, nombre, email FROM usuario"


    val resultSet = statement.executeQuery(query)

}