package ucentral.edu.sadm.perfil.infraestructura.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos editables del perfil. El correo no viaja (es de solo lectura).
 * Los tres campos de contraseña son opcionales en bloque: se validan en el servicio.
 */
public record perfilActualizacionEntidad(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        @Pattern(regexp = "^[\\p{L} ]+$", message = "El nombre solo puede contener letras")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 3, max = 50, message = "El apellido debe tener entre 3 y 50 caracteres")
        @Pattern(regexp = "^[\\p{L} ]+$", message = "El apellido solo puede contener letras")
        String apellido,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "El teléfono debe contener entre 7 y 15 dígitos")
        String telefono,

        @NotBlank(message = "La ubicación es obligatoria")
        @Size(min = 5, max = 150, message = "La ubicación debe tener entre 5 y 150 caracteres")
        String ubicacion,

        String contrasenaActual,
        String contrasenaNueva,
        String confirmarContrasena,
        Boolean quitarFoto
) {
}
