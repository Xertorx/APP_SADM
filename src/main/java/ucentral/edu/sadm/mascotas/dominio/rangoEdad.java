package ucentral.edu.sadm.mascotas.dominio;

/**
 * Categorías de edad para filtrar mascotas (HU09). Es el ÚNICO lugar donde se definen
 * los rangos: para cambiarlos basta editar las constantes (edad en años, MESES / 12).
 */
public enum rangoEdad {
    CACHORRO(0, 1),
    JOVEN(1, 3),
    ADULTO(3, 8),
    SENIOR(8, Double.POSITIVE_INFINITY);

    private final double minimoAnios;
    private final double maximoAnios;

    rangoEdad(double minimoAnios, double maximoAnios) {
        this.minimoAnios = minimoAnios;
        this.maximoAnios = maximoAnios;
    }

    /** Mínimo incluido, máximo excluido: Joven = 1 a menos de 3 años. */
    public boolean contiene(double anios) {
        return anios >= minimoAnios && anios < maximoAnios;
    }

    /** Edad de la mascota en años, convirtiendo si está guardada en meses. */
    public static double aAnios(Integer edad, String unidadEdad) {
        if (edad == null) {
            return -1;
        }
        return "MESES".equals(unidadEdad) ? edad / 12.0 : edad;
    }
}
