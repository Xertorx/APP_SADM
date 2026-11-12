package ucentral.edu.sadm.administracion.infraestructura.dto;

import ucentral.edu.sadm.usuarios.dominio.rolUsuario;

import java.util.Date;

/** Fila de la lista de usuarios (HU27). Nunca incluye passwordHash ni identificacion. */
public record usuarioAdminEntidad(
        Long id,
        String nombre,
        String apellido,
        String correo,
        rolUsuario rol,
        String estado,
        Date fechaRegistro
) {
}
