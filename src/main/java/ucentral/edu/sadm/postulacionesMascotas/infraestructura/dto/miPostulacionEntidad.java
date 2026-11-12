package ucentral.edu.sadm.postulacionesMascotas.infraestructura.dto;

import java.time.Instant;

/**
 * HU21: fila de "Mis postulaciones". No se usa @JsonInclude(NON_NULL) a propósito:
 * primeraFotoId debe salir como null cuando la mascota no tiene fotos.
 * Nunca incluye correo ni teléfono del dador.
 */
public record miPostulacionEntidad(
        Long id,
        Instant fechaPostulacion,
        Long mascotaId,
        String mascotaNombre,
        Long primeraFotoId,
        String dadorNombre,
        String nombreProceso,
        int etapaActual,
        int totalEtapas,
        int puntaje,
        String estado,
        String estadoEtiqueta,
        String estadoDetalle,
        Instant fechaActualizacion
) {
}
