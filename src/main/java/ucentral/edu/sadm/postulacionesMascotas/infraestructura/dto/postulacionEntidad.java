package ucentral.edu.sadm.postulacionesMascotas.infraestructura.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record postulacionEntidad(
        @NotNull(message = "La mascota es obligatoria", groups = Solicitud.class)
        @Positive(message = "El identificador de la mascota debe ser positivo", groups = Solicitud.class)
        @Digits(integer = 19, fraction = 0, message = "El identificador de la mascota debe ser un número entero válido", groups = Solicitud.class)
        Long mascotaId,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        @NotNull(message = "El identificador de la postulación es obligatorio")
        @Positive(message = "El identificador de la postulación debe ser positivo")
        Long id,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        @NotNull(message = "El identificador del adoptante es obligatorio")
        @Positive(message = "El identificador del adoptante debe ser positivo")
        Long adoptanteId,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        @NotNull(message = "La fecha de postulación es obligatoria")
        Instant fecha,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        @NotBlank(message = "El estado de la postulación es obligatorio")
        String estado,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        @NotNull(message = "El puntaje es obligatorio")
        @Min(value = 0, message = "El puntaje no puede ser negativo")
        Integer puntaje,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        @Valid
        MascotaResumen mascota,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        @NotNull(message = "Las etapas del proceso son obligatorias")
        @Valid
        List<EtapaProceso> etapas,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        @NotNull(message = "Debe indicarse si el adoptante puede postularse")
        Boolean puedePostularse,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        String motivo,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Boolean postulacionActiva,
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        String mensaje
) {
    public interface Solicitud {
    }

    public static postulacionEntidad creada(
            Long id,
            Long mascotaId,
            Long adoptanteId,
            Instant fecha,
            String estado,
            Integer puntaje
    ) {
        return new postulacionEntidad(
                mascotaId, id, adoptanteId, fecha, estado, puntaje,
                null, null, null, null, true, "Postulación creada correctamente"
        );
    }

    public static postulacionEntidad resumen(
            MascotaResumen mascota,
            List<EtapaProceso> etapas,
            boolean puedePostularse,
            String motivo,
            boolean postulacionActiva,
            String estado,
            String mensaje
    ) {
        return new postulacionEntidad(
                null, null, null, null, estado, null, mascota, etapas,
                puedePostularse, motivo, postulacionActiva, mensaje
        );
    }

    public record MascotaResumen(
            @NotNull(message = "El identificador de la mascota es obligatorio")
            @Positive(message = "El identificador de la mascota debe ser positivo")
            Long id,
            @NotBlank(message = "El nombre de la mascota es obligatorio")
            String nombre,
            String foto,
            String especie,
            String raza
    ) {
    }

    public record EtapaProceso(
            @NotBlank(message = "El nombre de la etapa es obligatorio")
            String nombre,
            @NotBlank(message = "La descripción de la etapa es obligatoria")
            String descripcion
    ) {
    }
}
