package interfaz;

import negocio.Negocio;
import java.util.Scanner;

/** Interfaz de consola; valida el formato antes de enviar datos al negocio. */
public class Main {
    private static final Scanner teclado = new Scanner(System.in);
    private static String leer(String mensaje) {
        System.out.println(mensaje); return teclado.nextLine().trim();
    }
    private static int entero(String mensaje) {
        String texto = leer(mensaje);
        if (texto.isEmpty()) throw new IllegalArgumentException("El campo es obligatorio: " + mensaje);
        try { return Integer.parseInt(texto); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Ingrese un entero valido."); }
    }
    private static int costo() {
        String texto = leer("Costo unitario (USD):");
        try {
            double precio = Double.parseDouble(texto);
            if (!(precio > 0 && precio <= 99999.99)) {
                throw new IllegalArgumentException("Precio fuera del rango permitido.");
            }
            return (int) Math.round(precio * 100);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ingrese un precio numerico valido.");
        }
    }
    public static void main(String[] args) {
        Negocio negocio = new Negocio();
        System.out.println("INVENTARIO DE REPUESTOS | Prototipo");
        while (true) {
            System.out.println("1 Registro | 2 Entrada | 3 Salida | 4 Listar | 5 Stock bajo | 6 Vencidos | 0 Salir");
            if (!teclado.hasNextLine()) break;
            try {
                int opcion = entero("Opcion:");
                if (opcion == 0) { System.out.println("Fin de sesion."); break; }
                String codigo;
                switch (opcion) {
                    case 1:
                        codigo = leer("Codigo:"); String nombre = leer("Nombre:");
                        int cantidad = entero("Cantidad:"), precio = costo(), fecha = entero("Ingreso AAAAMMDD:");
                        int tipo = entero("Tipo (1 con vencimiento, 2 sin vencimiento):");
                        int vence = tipo == 1 ? entero("Vencimiento AAAAMMDD:") : 0;
                        System.out.println(negocio.registrar(codigo, nombre, cantidad, precio, fecha, tipo, vence)); break;
                    case 2: System.out.println(negocio.entrada(leer("Codigo:"), entero("Cantidad:"))); break;
                    case 3: System.out.println(negocio.salida(leer("Codigo:"), entero("Cantidad:"), entero("Fecha AAAAMMDD:"))); break;
                    case 4: System.out.println(negocio.listar()); break;
                    case 5: System.out.println(negocio.bajos()); break;
                    case 6: System.out.println(negocio.vencidos(entero("Fecha de consulta AAAAMMDD:"))); break;
                    default: System.out.println("ERROR: opcion invalida.");
                }
            } catch (IllegalArgumentException e) { System.out.println("ERROR: " + e.getMessage()); }
            if (!teclado.hasNextLine()) break;
        }
        teclado.close();
    }
}
