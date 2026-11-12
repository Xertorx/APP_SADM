package ucentral.edu.sadm.administracion.infraestructura.dto;

import java.time.Instant;

public record moderacionHistorialEntidad(
        Instant fecha,
        String accion,
        String motivo,
        String adminNombre
) {
}
