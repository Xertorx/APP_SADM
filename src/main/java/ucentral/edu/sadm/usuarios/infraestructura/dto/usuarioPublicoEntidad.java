package ucentral.edu.sadm.usuarios.infraestructura.dto;

import ucentral.edu.sadm.usuarios.dominio.rolUsuario;

public record usuarioPublicoEntidad(
        Long id,
        String nombre,
        String apellido,
        String correo,
        String telefono,
        String direccion,
        rolUsuario rol,
        String estado
) {
}
