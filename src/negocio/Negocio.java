package negocio;

/** Reglas del inventario: capacidad, presupuesto, fechas y existencia. */
public class Negocio {
    private final modelo.Modelo.Producto[] productos = new modelo.Modelo.Producto[100];
    private int contador = 0;
    public static final int CAPACIDAD = 100, PRESUPUESTO = 50000, MINIMO = 5;

    public modelo.Modelo.Producto buscar(String codigo) {
        for (int i = 0; i < contador; i++) {
            if (productos[i].getCodigo().equalsIgnoreCase(codigo.trim())) {
                return productos[i];
            }
        }
        return null;
    }
    public int unidades() {
        int total = 0;
        for (int i = 0; i < contador; i++) {
            total += productos[i].getCantidad();
        }
        return total;
    }
    public int valor() {
        int total = 0;
        for (int i = 0; i < contador; i++) {
            total += productos[i].getCantidad() * productos[i].getCosto();
        }
        return total;
    }
    private String validarEntrada(int cantidad, int costo) {
        if (cantidad <= 0 || costo <= 0) return "ERROR: cantidad y costo deben ser positivos.";
        if (cantidad > CAPACIDAD - unidades()) return "ERROR: capacidad de 100 unidades superada.";
        if ((long)cantidad * costo > PRESUPUESTO - valor()) return "ERROR: presupuesto de $500.00 superado.";
        return null;
    }
    public String registrar(String codigo, String nombre, int cantidad, int costo,
                            int fecha, int tipo, int vencimiento) {
        if (codigo.trim().isEmpty() || nombre.trim().isEmpty()) return "ERROR: codigo y nombre obligatorios.";
        if (buscar(codigo) != null) return "ERROR: codigo duplicado.";
        if (contador == productos.length) return "ERROR: limite de 100 registros alcanzado.";
        if (tipo != 1 && tipo != 2) return "ERROR: tipo invalido.";
        if (!fechaValida(fecha) || (tipo == 1 && (!fechaValida(vencimiento) || vencimiento <= fecha)))
            return "ERROR: fecha invalida o vencimiento anterior/igual al ingreso.";
        String error = validarEntrada(cantidad, costo);
        if (error != null) return error;
        modelo.Modelo.Producto p;
        if (tipo == 1) p = new modelo.Modelo.Perecedero(codigo.trim(), nombre.trim(), cantidad, costo, fecha, vencimiento);
        else p = new modelo.Modelo.NoPerecedero(codigo.trim(), nombre.trim(), cantidad, costo, fecha);
        productos[contador] = p;
        contador++;
        return "OK: producto registrado. Stock: " + p.getCantidad();
    }
    public String entrada(String codigo, int cantidad) {
        modelo.Modelo.Producto p = buscar(codigo);
        if (p == null) return "ERROR: producto no encontrado.";
        String error = validarEntrada(cantidad, p.getCosto());
        if (error != null) return error;
        p.setCantidad(p.getCantidad() + cantidad);
        return "OK: entrada registrada. Stock: " + p.getCantidad();
    }
    public String salida(String codigo, int cantidad, int fecha) {
        modelo.Modelo.Producto p = buscar(codigo);
        if (p == null) return "ERROR: producto no encontrado.";
        if (!fechaValida(fecha) || fecha < p.getFechaIngreso()) return "ERROR: fecha de salida invalida.";
        if (cantidad <= 0) return "ERROR: cantidad debe ser positiva.";
        if (p.vencido(fecha)) return "ERROR: lote vencido.";
        if (cantidad > p.getCantidad()) return "ERROR: stock insuficiente.";
        p.setCantidad(p.getCantidad() - cantidad);
        return "OK: salida registrada. Stock: " + p.getCantidad();
    }
    public String listar() {
        String texto = "INVENTARIO\n";
        for (int i = 0; i < contador; i++) {
            modelo.Modelo.Producto p = productos[i];
            texto += p.getCodigo() + " | " + p.getNombre() + " | " + p.tipo() + " | stock: " + p.getCantidad()
                    + " | costo: " + dinero(p.getCosto()) + "\n";
        }
        return texto + "Unidades: " + unidades() + " | Valor: " + dinero(valor());
    }
    public String bajos() {
        String texto = "STOCK BAJO (<=5)\n"; boolean hay = false;
        for (int i = 0; i < contador; i++) {
            modelo.Modelo.Producto p = productos[i];
            if (p.getCantidad() <= MINIMO) {
                texto += p.getCodigo() + " | " + p.getNombre() + " | stock: " + p.getCantidad() + "\n";
                hay = true;
            }
        }
        return hay ? texto.trim() : texto + "Sin alertas.";
    }
    public String vencidos(int fecha) {
        if (!fechaValida(fecha)) return "ERROR: fecha invalida.";
        String texto = "LOTES VENCIDOS\n"; boolean hay = false;
        for (int i = 0; i < contador; i++) {
            modelo.Modelo.Producto p = productos[i];
            if (p.getCantidad() > 0 && p.vencido(fecha)) {
                texto += p.getCodigo() + " | " + p.getNombre() + " | stock: " + p.getCantidad() + "\n";
                hay = true;
            }
        }
        return hay ? texto.trim() : texto + "Sin alertas.";
    }
    public static String dinero(int centavos) {
        return "$" + (centavos / 100) + "." + (centavos % 100 < 10 ? "0" : "") + centavos % 100;
    }
    // Fecha AAAAMMDD, sin librerias adicionales.
    public static boolean fechaValida(int f) {
        int a = f / 10000, m = f / 100 % 100, d = f % 100;
        if (a < 2000 || a > 2099 || m < 1 || m > 12 || d < 1) return false;
        int[] dias = {31,28,31,30,31,30,31,31,30,31,30,31};
        if (a % 4 == 0 && (a % 100 != 0 || a % 400 == 0)) dias[1] = 29;
        return d <= dias[m-1];
    }
}