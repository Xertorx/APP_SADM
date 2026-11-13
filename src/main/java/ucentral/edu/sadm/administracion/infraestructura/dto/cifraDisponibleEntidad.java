package ucentral.edu.sadm.administracion.infraestructura.dto;

/**
 * HU30: una cifra que puede no estar disponible todavía (módulo aún sin implementar).
 * Si disponible = false el valor es 0 y el front lo muestra en gris.
 */
public record cifraDisponibleEntidad(long valor, boolean disponible) {
}
