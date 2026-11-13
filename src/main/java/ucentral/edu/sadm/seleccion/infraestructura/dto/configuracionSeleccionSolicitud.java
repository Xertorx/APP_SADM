package ucentral.edu.sadm.seleccion.infraestructura.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import ucentral.edu.sadm.seleccion.dominio.tipoRespuestaSeleccion;

import java.util.List;

public record configuracionSeleccionSolicitud(
        @NotNull @Valid etapaPreguntas preguntas,
        @NotNull @Valid etapaDocumentos documentos,
        @NotNull @Valid etapaEvaluacion entrevista,
        @NotNull @Valid etapaEvaluacion visitaDomiciliaria
) {
    public record etapaPreguntas(
            @NotNull @PositiveOrZero @Max(100) Integer puntajeMaximo,
            @NotNull List<@Valid preguntaSolicitud> elementos
    ) {
    }

    public record etapaDocumentos(
            @NotNull @PositiveOrZero @Max(100) Integer puntajeMaximo,
            @NotNull List<@Valid documentoSolicitud> elementos
    ) {
    }

    public record etapaEvaluacion(
            @NotNull @PositiveOrZero @Max(100) Integer puntajeMaximo,
            @NotNull List<@Valid criterioSolicitud> criterios
    ) {
    }

    public record preguntaSolicitud(
            Long id,
            @NotBlank @Size(max = 500) String texto,
            @NotNull tipoRespuestaSeleccion tipoRespuesta,
            @NotNull @PositiveOrZero @Max(100) Integer puntaje,
            @NotNull @Size(max = 20) List<@NotBlank @Size(max = 200) String> opciones
    ) {
        @AssertTrue(message = "Las preguntas de selección única o múltiple necesitan al menos dos opciones")
        public boolean isOpcionesCompatibles() {
            if (tipoRespuesta == null || opciones == null) {
                return true;
            }
            boolean seleccion = tipoRespuesta == tipoRespuestaSeleccion.SELECCION_UNICA
                    || tipoRespuesta == tipoRespuestaSeleccion.SELECCION_MULTIPLE;
            return seleccion ? opciones.size() >= 2 : opciones.isEmpty();
        }
    }

    public record documentoSolicitud(
            Long id,
            @NotBlank @Size(max = 150) String nombre,
            @Size(max = 1000) String descripcion,
            @NotNull Boolean obligatorio,
            @NotNull @PositiveOrZero @Max(100) Integer puntajeMaximo
    ) {
    }

    public record criterioSolicitud(
            Long id,
            @NotBlank @Size(max = 1000) String descripcion,
            @NotNull @PositiveOrZero @Max(100) Integer puntajeMaximo
    ) {
    }
}
