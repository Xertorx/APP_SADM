package ucentral.edu.sadm.mascotas.infraestructura.dto;

import java.util.Date;
import java.util.List;

/**
 * HU10: resultado de /api/mascotas/buscar cuando se envían lat y lon.
 * Mismos campos que mascotaDetalleEntidad + distanciaKm. A propósito NO lleva latitud/longitud
 * de la mascota (es la casa del dador): solo la distancia aproximada. null = la mascota no tiene coordenadas.
 */
public record mascotaBusquedaEntidad(
        Long id,
        String nombre,
        String especie,
        String raza,
        Integer edad,
        String unidadEdad,
        String tamano,
        String sexo,
        List<String> temperamento,
        String historialSalud,
        String ubicacion,
        String estado,
        Date fechaPublicacion,
        Long idDador,
        List<Long> fotosIds,
        Double distanciaKm
) {
    public static mascotaBusquedaEntidad de(mascotaDetalleEntidad d, Double distanciaKm) {
        return new mascotaBusquedaEntidad(
                d.id(), d.nombre(), d.especie(), d.raza(), d.edad(), d.unidadEdad(), d.tamano(), d.sexo(),
                d.temperamento(), d.historialSalud(), d.ubicacion(), d.estado(), d.fechaPublicacion(),
                d.idDador(), d.fotosIds(), distanciaKm);
    }
}
