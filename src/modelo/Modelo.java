package modelo;

/** Entidades del inventario. Los precios se mantienen en centavos enteros. */
public class Modelo {
    public static abstract class Producto {
        private final String codigo, nombre;
        private final int costo, fechaIngreso;
        private int cantidad;
        protected Producto(String codigo, String nombre, int cantidad, int costo, int fechaIngreso) {
            this.codigo = codigo; this.nombre = nombre; this.cantidad = cantidad;
            this.costo = costo; this.fechaIngreso = fechaIngreso;
        }
        public String getCodigo() { return codigo; }
        public String getNombre() { return nombre; }
        public int getCantidad() { return cantidad; }
        public int getCosto() { return costo; }
        public int getFechaIngreso() { return fechaIngreso; }
        public void setCantidad(int nueva) {
            if (nueva < 0) throw new IllegalArgumentException("Cantidad negativa.");
            cantidad = nueva;
        }
        public abstract String tipo();
        public abstract boolean vencido(int fecha);
    }
    public static class Perecedero extends Producto {
        private final int vencimiento;
        public Perecedero(String c, String n, int q, int costo, int fecha, int vencimiento) {
            super(c, n, q, costo, fecha); this.vencimiento = vencimiento;
        }
        public int getVencimiento() { return vencimiento; }
        @Override public String tipo() { return "Con vencimiento"; }
        @Override public boolean vencido(int fecha) { return fecha >= vencimiento; }
    }
    public static class NoPerecedero extends Producto {
        public NoPerecedero(String c, String n, int q, int costo, int fecha) {
            super(c, n, q, costo, fecha);
        }
        @Override public String tipo() { return "Sin vencimiento"; }
        @Override public boolean vencido(int fecha) { return false; }
    }
}
