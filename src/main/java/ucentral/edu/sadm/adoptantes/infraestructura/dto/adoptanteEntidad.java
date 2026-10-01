package ucentral.edu.sadm.adoptantes.infraestructura.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Date;

public record adoptanteEntidad (
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        @Pattern(regexp = "^[\\p{L}]+$", message = "El nombre solo puede contener letras")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 3, max = 50, message = "El apellido debe tener entre 3 y 50 caracteres")
        @Pattern(regexp = "^[\\p{L}]+$", message = "El apellido solo puede contener letras")
         String apellido,

        @NotNull(message = "La identificación es obligatoria")
        @Positive(message = "La identificación debe ser un número positivo")
        @Digits(integer = 10, fraction = 0, message = "La identificación debe tener máximo 10 dígitos")
         Integer identificacion,

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
         String direccion,

        @NotBlank(message = "El tipo de usuario es obligatorio")
        @Size(max = 30, message = "El tipo de usuario no puede superar los 30 caracteres")
         String tipo_usuario,

        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 20, message = "El estado no puede superar los 20 caracteres")
         String estado) {


}
