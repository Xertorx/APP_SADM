package ucentral.edu.sadm.perfil.infraestructura.dto;

import java.util.Date;

/**
 * Datos del dador que ve cualquier usuario autenticado en el detalle de una mascota.
 * Nunca incluye correo, teléfono, dirección, identificación ni contraseña.
 */
public record perfilPublicoEntidad(
        Long id,
        String nombre,
        String apellido,
        boolean tieneFoto,
        Date miembroDesde
) {
}
