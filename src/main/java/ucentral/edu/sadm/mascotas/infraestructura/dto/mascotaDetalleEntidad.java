package ucentral.edu.sadm.mascotas.infraestructura.dto;

import java.util.Date;
import java.util.List;

public record mascotaDetalleEntidad(
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
        List<Long> fotosIds
) {
}
