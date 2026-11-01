package ucentral.edu.sadm.mascotas.infraestructura.dto;

import java.util.Date;

public record mascotaEliminacionEntidad(
        Long id,
        String nombre,
        String especie,
        String raza,
        Integer edad,
        String unidadEdad,
        Date fechaPublicacion,
        Long primeraFotoId,
        long postulacionesActivas
) {
}
