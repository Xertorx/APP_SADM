package ucentral.edu.sadm.ubicaciones.dominio;

/** HU10: una localidad de Bogotá con las coordenadas aproximadas de su centro. */
public record localidad(String codigo, String nombre, double latitud, double longitud) {
}
