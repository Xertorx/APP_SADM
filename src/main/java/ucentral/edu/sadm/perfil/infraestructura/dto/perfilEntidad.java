package ucentral.edu.sadm.perfil.infraestructura.dto;

import java.time.Instant;

/** Respuesta del perfil. Nunca incluye la contraseña ni su hash. */
public record perfilEntidad(
        Long id,
        String nombre,
        String apellido,
        String correo,
        String telefono,
        String ubicacion,
        boolean tieneFoto,
        Instant fotoActualizadaEn
) {
}
