package ucentral.edu.sadm.usuarios.infraestructura.dto;

import jakarta.validation.constraints.*;

import java.util.Date;

public record usuarioRegistroEntidad(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        @Pattern(regexp = "^[\\p{L} ]+$", message = "El nombre solo puede contener letras")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 3, max = 50, message = "El apellido debe tener entre 3 y 50 caracteres")
        @Pattern(regexp = "^[\\p{L} ]+$", message = "El apellido solo puede contener letras")
        String apellido,

        @NotNull(message = "La identificacion es obligatoria")
        @Positive(message = "La identificacion debe ser un numero positivo")
        @Min(value = 1, message = "La identificacion debe tener al menos 1 digito")
        @Max(value = 9_999_999_999L, message = "La identificacion debe tener maximo 10 digitos")
        @Digits(integer = 10, fraction = 0, message = "La identificacion debe tener maximo 10 digitos")
        Long identificacion,

        @NotNull(message = "La fecha de nacimiento es requerida")
        @Past(message = "La fecha de nacimiento debe ser anterior a la fecha actual")
        Date fecha_nacimiento,

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
