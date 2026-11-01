package ucentral.edu.sadm.fundaciones.infraestructura.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record fundacionRegistroEntidad(
        @NotBlank(message = "El nombre de la fundación es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String nombre,

        @NotBlank(message = "El NIT es obligatorio")
        @Pattern(regexp = "^[0-9]{6,15}(-[0-9])?$", message = "El NIT debe tener entre 6 y 15 dígitos y un dígito de verificación opcional")
        String nit,

        @NotBlank(message = "El nombre del representante es obligatorio")
        @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        String nombreRepresentante,

        @NotBlank(message = "El apellido del representante es obligatorio")
        @Size(min = 3, max = 50, message = "El apellido debe tener entre 3 y 50 caracteres")
        String apellidoRepresentante,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 254, message = "El correo no puede superar los 254 caracteres")
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "La contraseña debe contener al menos una letra y un número")
        String contraseña,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "El teléfono debe contener entre 7 y 15 dígitos")
        String telefono,

        @NotBlank(message = "La dirección es obligatoria")
        @Size(min = 5, max = 150, message = "La dirección debe tener entre 5 y 150 caracteres")
        String direccion
) {
}
