package ucentral.edu.sadm.mascotas.infraestructura.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record mascotaEntidad(

        // TODO HU02: tomar del usuario autenticado en vez de recibirlo del cliente
        @NotNull(message = "El id del dador es obligatorio")
        @Positive(message = "El id del dador debe ser un número positivo")
        Long idDador,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
        String nombre,

        @NotBlank(message = "La especie es obligatoria")
        @Pattern(regexp = "PERRO|GATO|OTRO", message = "La especie debe ser PERRO, GATO u OTRO")
        String especie,

        @Size(max = 50, message = "La raza no puede superar los 50 caracteres")
        String raza,

        @NotNull(message = "La edad es obligatoria")
        @Positive(message = "La edad debe ser un número entero mayor a 0")
        Integer edad,

        @NotBlank(message = "La unidad de edad es obligatoria")
        @Pattern(regexp = "MESES|ANIOS", message = "La unidad de edad debe ser MESES o ANIOS")
        String unidadEdad,

        @NotBlank(message = "El tamaño es obligatorio")
        @Pattern(regexp = "PEQUENO|MEDIANO|GRANDE", message = "El tamaño debe ser PEQUENO, MEDIANO o GRANDE")
        String tamano,

        @NotBlank(message = "El sexo es obligatorio")
        @Pattern(regexp = "MACHO|HEMBRA", message = "El sexo debe ser MACHO o HEMBRA")
        String sexo,

        @NotEmpty(message = "Debes seleccionar al menos un temperamento")
        List<
                @Pattern(
                        regexp = "JUGUETON|TRANQUILO|TIMIDO|SOCIABLE|PROTECTOR|ENERGICO|CARINOSO",
                        message = "Selecciona un temperamento válido"
                )
                String
        > temperamento,

        @Size(max = 1000, message = "El historial de salud no puede superar los 1000 caracteres")
        String historialSalud,

        @NotBlank(message = "La ubicación es obligatoria")
        @Size(min = 3, max = 100, message = "La ubicación debe tener entre 3 y 100 caracteres")
        String ubicacion
) {
}
