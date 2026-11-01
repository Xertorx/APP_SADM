package ucentral.edu.sadm.fundaciones.aplicacion;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ucentral.edu.sadm.common.infraestructure.BusinessException;
import ucentral.edu.sadm.common.service.PasswordService;
import ucentral.edu.sadm.fundaciones.dominio.fundacion;
import ucentral.edu.sadm.fundaciones.dominio.fundacionRepositorio;
import ucentral.edu.sadm.fundaciones.infraestructura.dto.fundacionPublicaEntidad;
import ucentral.edu.sadm.fundaciones.infraestructura.dto.fundacionRegistroEntidad;
import ucentral.edu.sadm.usuarios.dominio.exception.EmailAlreadyExistsException;
import ucentral.edu.sadm.usuarios.dominio.rolUsuario;
import ucentral.edu.sadm.usuarios.dominio.usuario;
import ucentral.edu.sadm.usuarios.dominio.usuarioRepositorio;

import java.util.Locale;
import java.util.List;

@ApplicationScoped
public class fundacionServicio {

    @Inject
    usuarioRepositorio usuarioRepositorio;

    @Inject
    fundacionRepositorio fundacionRepositorio;

    @Inject
    PasswordService passwordService;

    @Transactional
    public Long crear(fundacionRegistroEntidad datos) {
        String correo = datos.correo().trim().toLowerCase(Locale.ROOT);
        String nit = datos.nit().trim();
        if (usuarioRepositorio.findByEmail(correo) != null) {
            throw new EmailAlreadyExistsException();
        }
        if (fundacionRepositorio.findByNit(nit) != null) {
            throw new BusinessException(409, "El NIT ingresado ya está registrado");
        }

        usuario cuenta = new usuario();
        cuenta.nombre = datos.nombreRepresentante().trim();
        cuenta.apellido = datos.apellidoRepresentante().trim();
        cuenta.correo = correo;
        cuenta.passwordHash = passwordService.hash(datos.contraseña());
        cuenta.telefono = datos.telefono().trim();
        cuenta.direccion = datos.direccion().trim();
        cuenta.rol = rolUsuario.FUNDACION;
        cuenta.estado = "ACTIVO";
        usuarioRepositorio.persist(cuenta);

        fundacion fundacion = new fundacion();
        fundacion.usuario = cuenta;
        fundacion.nombre = datos.nombre().trim();
        fundacion.nit = nit;
        fundacion.estado = "PENDIENTE_REVISION";
        fundacionRepositorio.persist(fundacion);
        return fundacion.id;
    }

    @Transactional
    public void aprobar(Long id) {
        fundacion fundacion = fundacionRepositorio.findById(id);
        if (fundacion == null) {
            throw new BusinessException(404, "La fundación no existe");
        }
        fundacion.estado = "APROBADA";
    }

    @Transactional
    public List<fundacionPublicaEntidad> consultarTodas() {
        return fundacionRepositorio.listAll().stream()
                .map(fundacion -> new fundacionPublicaEntidad(
                        fundacion.id,
                        fundacion.nombre,
                        fundacion.nit,
                        fundacion.usuario.correo,
                        fundacion.estado
                ))
                .toList();
    }
}
