class Libro(
    private var titulo: String, private var calif: Double, private var autor: String, private var fech_lanz: String,
    private var precio: Double
) {
    override fun toString(): String {
        return "Libro(titulo=$titulo, calif=$calif, autor =$autor, fech_lanz=$fech_lanz, precio=$precio)"
    }
}