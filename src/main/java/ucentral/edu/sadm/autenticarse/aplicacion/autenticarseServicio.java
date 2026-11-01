package ucentral.edu.sadm.autenticarse.aplicacion;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ucentral.edu.sadm.common.infraestructure.BusinessException;
import ucentral.edu.sadm.common.service.PasswordService;
import ucentral.edu.sadm.autenticarse.infraestructura.dto.loginRespuesta;
import ucentral.edu.sadm.autenticarse.infraestructura.dto.loginSolicitud;
import ucentral.edu.sadm.autenticarse.infraestructura.seguridad.jwtServicio;
import ucentral.edu.sadm.usuarios.dominio.usuario;
import ucentral.edu.sadm.usuarios.dominio.usuarioRepositorio;

@ApplicationScoped
public class autenticarseServicio {

    @Inject
    usuarioRepositorio usuarioRepositorio;

    @Inject
    PasswordService passwordService;

    @Inject
    jwtServicio jwtServicio;

    public loginRespuesta iniciarSesion(loginSolicitud solicitud) {
        usuario usuario = usuarioRepositorio.findByEmail(solicitud.correo());
        if (usuario == null
                || !"ACTIVO".equalsIgnoreCase(usuario.estado)
                || !passwordService.verificar(solicitud.contraseña(), usuario.passwordHash)) {
            throw new BusinessException(401, "Correo o contraseña incorrectos");
        }

        jwtServicio.TokenEmitido token = jwtServicio.emitir(usuario);
        return new loginRespuesta(token.token(), "Bearer", token.expiraEnSegundos());
    }
}
