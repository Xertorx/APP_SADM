package ucentral.edu.sadm.administracion.infraestructura.dto;

import java.util.Map;

/**
 * HU30: respuesta del panel de estadísticas. Es un record (no una entidad) para no serializar
 * nada de Hibernate. desde/hasta son null cuando no se aplicó filtro (histórico).
 * Fechas como texto: desde/hasta "yyyy-MM-dd", generadoEn "yyyy-MM-dd'T'HH:mm:ss".
 */
public record estadisticasEntidad(
        long mascotasPublicadas,
        Map<String, Long> mascotasPorEstado,
        cifraDisponibleEntidad adopcionesCompletadas,
        Map<String, Long> usuariosActivosPorRol,
        long postulacionesEnCurso,
        cifraDisponibleEntidad adopcionesEnSeguimiento,
        String desde,
        String hasta,
        String generadoEn) {
}
