class Libro(
    var titulo: String, var calif: Double, var autor: String, var fech_lanz: String,
    var precio: Double
) {
    override fun toString(): String {
        return "$titulo,$calif,$autor,$fech_lanz,$precio"
    }
}