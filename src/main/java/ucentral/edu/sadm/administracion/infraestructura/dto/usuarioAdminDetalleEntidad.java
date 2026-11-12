package ucentral.edu.sadm.administracion.infraestructura.dto;

import ucentral.edu.sadm.usuarios.dominio.rolUsuario;

import java.util.Date;
import java.util.List;

/** Detalle de un usuario para el administrador (HU27). Sin passwordHash ni identificacion. */
public record usuarioAdminDetalleEntidad(
        Long id,
        String nombre,
        String apellido,
        String correo,
        rolUsuario rol,
        String estado,
        Date fechaRegistro,
        String telefono,
        String direccion,
        boolean tieneFoto,
        Actividad actividad,
        List<moderacionHistorialEntidad> historial
) {
    public record Actividad(long mascotasPublicadas, long postulaciones) {
    }
}
