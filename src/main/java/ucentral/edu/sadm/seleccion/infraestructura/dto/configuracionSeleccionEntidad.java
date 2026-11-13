package ucentral.edu.sadm.seleccion.infraestructura.dto;

import ucentral.edu.sadm.seleccion.dominio.tipoRespuestaSeleccion;

import java.util.List;

public record configuracionSeleccionEntidad(
        Long mascotaId,
        boolean definitiva,
        int puntajeDistribuido,
        int puntosPendientes,
        int puntosExcedentes,
        etapaPreguntas preguntas,
        etapaDocumentos documentos,
        etapaEvaluacion entrevista,
        etapaEvaluacion visitaDomiciliaria
) {
    public record etapaPreguntas(int puntajeMaximo, List<pregunta> elementos) {
    }

    public record pregunta(
            Long id,
            String texto,
            tipoRespuestaSeleccion tipoRespuesta,
            int puntaje,
            List<String> opciones
    ) {
    }

    public record etapaDocumentos(int puntajeMaximo, List<documento> elementos) {
    }

    public record documento(
            Long id,
            String nombre,
            String descripcion,
            boolean obligatorio,
            int puntajeMaximo
    ) {
    }

    public record etapaEvaluacion(int puntajeMaximo, List<criterio> criterios) {
    }

    public record criterio(Long id, String descripcion, int puntajeMaximo) {
    }
}
