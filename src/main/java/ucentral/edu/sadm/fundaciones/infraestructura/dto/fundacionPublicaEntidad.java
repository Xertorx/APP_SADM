package ucentral.edu.sadm.fundaciones.infraestructura.dto;

public record fundacionPublicaEntidad(
        Long id,
        String nombre,
        String nit,
        String correo,
        String estado
) {
}
