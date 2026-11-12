package ucentral.edu.sadm.administracion.infraestructura.dto;

/**
 * Cuerpo de POST /api/admin/usuarios/{id}/inhabilitar.
 * La validación del motivo (obligatorio, 10–300) se hace en adminUsuarioServicio para devolver
 * siempre un único mensaje claro (con @NotBlank + @Size, un motivo vacío fallaría las dos reglas
 * y el mapper de validación solo conserva un mensaje por campo).
 */
public record inhabilitarSolicitud(String motivo) {
}
