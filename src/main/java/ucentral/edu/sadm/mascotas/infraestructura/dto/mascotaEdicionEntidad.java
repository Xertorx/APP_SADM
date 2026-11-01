package ucentral.edu.sadm.mascotas.infraestructura.dto;

public record mascotaEdicionEntidad(
        mascotaDetalleEntidad mascota,
        boolean tienePostulacionesActivas,
        String advertencia
) {
}
