package ucentral.edu.sadm.ubicaciones.dominio;

import java.util.List;
import java.util.Optional;

/**
 * HU10: catálogo propio de las 20 localidades de Bogotá (sin proveedor externo de mapas ni claves).
 * Las coordenadas son APROXIMADAS (centro de cada localidad, 3 decimales): sirven para ordenar por cercanía,
 * no para navegar.
 */
public final class localidadesBogota {

    public static final List<localidad> TODAS = List.of(
            new localidad("USAQUEN", "Usaquén", 4.705, -74.031),
            new localidad("CHAPINERO", "Chapinero", 4.649, -74.063),
            new localidad("SANTA_FE", "Santa Fe", 4.602, -74.064),
            new localidad("SAN_CRISTOBAL", "San Cristóbal", 4.568, -74.085),
            new localidad("USME", "Usme", 4.475, -74.126),
            new localidad("TUNJUELITO", "Tunjuelito", 4.575, -74.133),
            new localidad("BOSA", "Bosa", 4.606, -74.185),
            new localidad("KENNEDY", "Kennedy", 4.630, -74.155),
            new localidad("FONTIBON", "Fontibón", 4.673, -74.145),
            new localidad("ENGATIVA", "Engativá", 4.702, -74.110),
            new localidad("SUBA", "Suba", 4.741, -74.083),
            new localidad("BARRIOS_UNIDOS", "Barrios Unidos", 4.673, -74.084),
            new localidad("TEUSAQUILLO", "Teusaquillo", 4.636, -74.090),
            new localidad("LOS_MARTIRES", "Los Mártires", 4.605, -74.087),
            new localidad("ANTONIO_NARINO", "Antonio Nariño", 4.590, -74.102),
            new localidad("PUENTE_ARANDA", "Puente Aranda", 4.618, -74.117),
            new localidad("LA_CANDELARIA", "La Candelaria", 4.596, -74.073),
            new localidad("RAFAEL_URIBE_URIBE", "Rafael Uribe Uribe", 4.560, -74.110),
            new localidad("CIUDAD_BOLIVAR", "Ciudad Bolívar", 4.500, -74.160),
            new localidad("SUMAPAZ", "Sumapaz", 4.200, -74.270)
    );

    private localidadesBogota() {
    }

    public static Optional<localidad> buscar(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        return TODAS.stream().filter(l -> l.codigo().equals(codigo)).findFirst();
    }
}
