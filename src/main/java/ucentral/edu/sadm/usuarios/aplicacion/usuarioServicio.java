package ucentral.edu.sadm.usuarios.aplicacion;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ucentral.edu.sadm.common.service.PasswordService;
import ucentral.edu.sadm.usuarios.dominio.rolUsuario;
import ucentral.edu.sadm.usuarios.dominio.usuario;
import ucentral.edu.sadm.usuarios.dominio.usuarioRepositorio;
import ucentral.edu.sadm.usuarios.dominio.exception.EmailAlreadyExistsException;
import ucentral.edu.sadm.usuarios.infraestructura.dto.usuarioPublicoEntidad;
import ucentral.edu.sadm.usuarios.infraestructura.dto.usuarioRegistroEntidad;

import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class usuarioServicio {

    @Inject
    usuarioRepositorio usuarioRepositorio;

    @Inject
    PasswordService passwordService;

    @Transactional
    public Long crear(usuarioRegistroEntidad datos) {
        String correo = normalizarCorreo(datos.correo());
        if (usuarioRepositorio.findByEmail(correo) != null) {
            throw new EmailAlreadyExistsException();
        }

        usuario usuario = new usuario();
        usuario.nombre = datos.nombre().trim();
        usuario.apellido = datos.apellido().trim();
        usuario.identificacion = datos.identificacion();
        usuario.fecha_nacimiento = datos.fecha_nacimiento();
        usuario.correo = correo;
        usuario.passwordHash = passwordService.hash(datos.contraseña());
        usuario.telefono = datos.telefono().trim();
        usuario.direccion = datos.direccion().trim();
        usuario.rol = rolUsuario.USUARIO;
        usuario.estado = "ACTIVO";
        usuarioRepositorio.persist(usuario);
        return usuario.id;
    }

    public List<usuarioPublicoEntidad> consultarTodos() {
        return usuarioRepositorio.listAll().stream()
                .map(usuario -> new usuarioPublicoEntidad(
                        usuario.id,
                        usuario.nombre,
                        usuario.apellido,
                        usuario.correo,
                        usuario.telefono,
                        usuario.direccion,
                        usuario.rol,
                        usuario.estado
                ))
                .toList();
    }

    private String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase(Locale.ROOT);
    }
}
