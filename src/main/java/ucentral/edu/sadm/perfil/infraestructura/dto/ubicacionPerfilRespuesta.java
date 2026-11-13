package ucentral.edu.sadm.perfil.infraestructura.dto;

import java.time.Instant;

/** HU10: ubicación de referencia del usuario autenticado (es SU propia ubicación, no la de otra persona). */
public record ubicacionPerfilRespuesta(
        Double latitud,
        Double longitud,
        String localidad,
        Instant fechaActualizacion
) {
}
