package ucentral.edu.sadm.perfil.aplicacion;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ucentral.edu.sadm.common.infraestructure.BusinessException;
import ucentral.edu.sadm.mascotas.dominio.distanciaGeografica;
import ucentral.edu.sadm.perfil.dominio.exception.PerfilNoEncontradoException;
import ucentral.edu.sadm.perfil.dominio.exception.UbicacionNoRegistradaException;
import ucentral.edu.sadm.perfil.dominio.ubicacionUsuario;
import ucentral.edu.sadm.perfil.dominio.ubicacionUsuarioRepositorio;
import ucentral.edu.sadm.perfil.infraestructura.dto.ubicacionPerfilEntidad;
import ucentral.edu.sadm.perfil.infraestructura.dto.ubicacionPerfilRespuesta;
import ucentral.edu.sadm.ubicaciones.dominio.localidadesBogota;
import ucentral.edu.sadm.usuarios.dominio.usuario;
import ucentral.edu.sadm.usuarios.dominio.usuarioRepositorio;

import java.time.Instant;

/** HU10: ubicación de referencia del perfil. El usuario siempre sale del token (correo). */
@ApplicationScoped
@Transactional
public class ubicacionPerfilServicio {

    @Inject
    usuarioRepositorio usuarioRepositorio;
    @Inject
    ubicacionUsuarioRepositorio ubicacionUsuarioRepositorio;

    public ubicacionPerfilRespuesta consultar(String correo) {
        usuario u = buscarUsuario(correo);
        ubicacionUsuario ubicacion = ubicacionUsuarioRepositorio.findByUsuarioId(u.id);
        if (ubicacion == null) {
            throw new UbicacionNoRegistradaException();
        }
        return aRespuesta(ubicacion);
    }

    /** Crea o actualiza (una sola fila por usuario). Las coordenadas se guardan a 3 decimales. */
    public ubicacionPerfilRespuesta guardar(ubicacionPerfilEntidad datos, String correo) {
        usuario u = buscarUsuario(correo);

        String codigoLocalidad = datos.localidad() == null || datos.localidad().isBlank()
                ? null : datos.localidad().trim();
        if (codigoLocalidad != null && localidadesBogota.buscar(codigoLocalidad).isEmpty()) {
            throw new BusinessException(400, "La localidad no es válida");
        }

        ubicacionUsuario ubicacion = ubicacionUsuarioRepositorio.findByUsuarioId(u.id);
        if (ubicacion == null) {
            ubicacion = new ubicacionUsuario();
            ubicacion.usuario = u;
        }
        ubicacion.latitud = distanciaGeografica.redondear(datos.latitud());
        ubicacion.longitud = distanciaGeografica.redondear(datos.longitud());
        ubicacion.localidad = codigoLocalidad;
        ubicacion.fecha_actualizacion = Instant.now();
        ubicacionUsuarioRepositorio.persist(ubicacion);
        return aRespuesta(ubicacion);
    }

    private usuario buscarUsuario(String correo) {
        usuario u = usuarioRepositorio.findByEmail(correo);
        if (u == null) {
            throw new PerfilNoEncontradoException();
        }
        return u;
    }

    private ubicacionPerfilRespuesta aRespuesta(ubicacionUsuario u) {
        return new ubicacionPerfilRespuesta(u.latitud, u.longitud, u.localidad, u.fecha_actualizacion);
    }
}
