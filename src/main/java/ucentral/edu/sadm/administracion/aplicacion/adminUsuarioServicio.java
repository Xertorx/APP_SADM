package ucentral.edu.sadm.administracion.aplicacion;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ucentral.edu.sadm.administracion.dominio.usuarioModeracion;
import ucentral.edu.sadm.administracion.dominio.usuarioModeracionRepositorio;
import ucentral.edu.sadm.administracion.infraestructura.dto.moderacionHistorialEntidad;
import ucentral.edu.sadm.administracion.infraestructura.dto.usuarioAdminDetalleEntidad;
import ucentral.edu.sadm.administracion.infraestructura.dto.usuarioAdminEntidad;
import ucentral.edu.sadm.common.infraestructure.BusinessException;
import ucentral.edu.sadm.mascotas.dominio.mascotaRepositorio;
import ucentral.edu.sadm.mascotas.dominio.textoNormalizado;
import ucentral.edu.sadm.perfil.dominio.fotoPerfilRepositorio;
import ucentral.edu.sadm.postulacionesMascotas.dominio.postulacionRepositorio;
import ucentral.edu.sadm.usuarios.dominio.rolUsuario;
import ucentral.edu.sadm.usuarios.dominio.usuario;
import ucentral.edu.sadm.usuarios.dominio.usuarioRepositorio;

import java.util.Comparator;
import java.util.List;

/**
 * HU27 + HU28: búsqueda/consulta de usuarios e inhabilitación/reactivación (solo ADMINISTRADOR).
 * Las consultas no llevan @Transactional y devuelven records (no entidades), así no se serializan
 * relaciones lazy fuera de la sesión. Solo inhabilitar/reactivar escriben y son transaccionales.
 */
@ApplicationScoped
public class adminUsuarioServicio {

    static final String ACTIVO = "ACTIVO";
    static final String INHABILITADO = "INHABILITADO";
    static final int MOTIVO_MIN = 10;
    static final int MOTIVO_MAX = 300;

    @Inject
    usuarioRepositorio usuarioRepositorio;
    @Inject
    usuarioModeracionRepositorio moderacionRepositorio;
    @Inject
    mascotaRepositorio mascotaRepositorio;
    @Inject
    postulacionRepositorio postulacionRepositorio;
    @Inject
    fotoPerfilRepositorio fotoPerfilRepositorio;

    /** Nombre vacío → todos. Coincidencia parcial en "nombre apellido", sin tildes ni mayúsculas. */
    public List<usuarioAdminEntidad> buscar(String nombre) {
        String buscado = textoNormalizado.normalizar(nombre);
        // La BD compartida no tiene unaccent: se filtra en Java, igual que HU08.
        return usuarioRepositorio.listAll().stream()
                .filter(u -> buscado.isEmpty() || nombreCompleto(u).contains(buscado))
                .sorted(Comparator.comparing(adminUsuarioServicio::nombreCompleto)
                        .thenComparing(u -> u.id))
                .map(this::aFila)
                .toList();
    }

    public usuarioAdminDetalleEntidad detalle(Long id) {
        return aDetalle(buscarOFallar(id));
    }

    @Transactional
    public usuarioAdminDetalleEntidad inhabilitar(Long id, String motivo, String correoAdmin) {
        usuario destino = buscarOFallar(id);
        usuario admin = usuarioRepositorio.findByEmail(correoAdmin);
        if (admin == null) {
            throw new BusinessException(401, "Sesión no válida");
        }
        if (admin.id.equals(destino.id)) {
            throw new BusinessException(400, "No puedes inhabilitar tu propia cuenta");
        }
        if (destino.rol == rolUsuario.ADMINISTRADOR) {
            throw new BusinessException(400, "No puedes inhabilitar a otro administrador");
        }
        String motivoLimpio = validarMotivo(motivo);
        if (INHABILITADO.equalsIgnoreCase(destino.estado)) {
            throw new BusinessException(409, "La cuenta ya está inhabilitada");
        }
        destino.estado = INHABILITADO;
        registrar(destino, admin, usuarioModeracion.INHABILITAR, motivoLimpio);
        return aDetalle(destino);
    }

    @Transactional
    public usuarioAdminDetalleEntidad reactivar(Long id, String correoAdmin) {
        usuario destino = buscarOFallar(id);
        usuario admin = usuarioRepositorio.findByEmail(correoAdmin);
        if (admin == null) {
            throw new BusinessException(401, "Sesión no válida");
        }
        if (ACTIVO.equalsIgnoreCase(destino.estado)) {
            throw new BusinessException(409, "La cuenta ya está activa");
        }
        destino.estado = ACTIVO;
        registrar(destino, admin, usuarioModeracion.REACTIVAR, null);
        return aDetalle(destino);
    }

    // ---- privados ----

    private usuario buscarOFallar(Long id) {
        usuario u = id == null ? null : usuarioRepositorio.findById(id);
        if (u == null) {
            throw new BusinessException(404, "El usuario no existe");
        }
        return u;
    }

    private String validarMotivo(String motivo) {
        String limpio = motivo == null ? "" : motivo.trim();
        if (limpio.isEmpty()) {
            throw new BusinessException(400, "El motivo es obligatorio");
        }
        if (limpio.length() < MOTIVO_MIN || limpio.length() > MOTIVO_MAX) {
            throw new BusinessException(400,
                    "El motivo debe tener entre " + MOTIVO_MIN + " y " + MOTIVO_MAX + " caracteres");
        }
        return limpio;
    }

    private void registrar(usuario destino, usuario admin, String accion, String motivo) {
        usuarioModeracion m = new usuarioModeracion();
        m.usuario = destino;
        m.admin = admin;
        m.accion = accion;
        m.motivo = motivo;
        moderacionRepositorio.persist(m);
    }

    private static String nombreCompleto(usuario u) {
        return textoNormalizado.normalizar(
                (u.nombre == null ? "" : u.nombre) + " " + (u.apellido == null ? "" : u.apellido));
    }

    private usuarioAdminEntidad aFila(usuario u) {
        return new usuarioAdminEntidad(u.id, u.nombre, u.apellido, u.correo, u.rol, u.estado, u.fecha_registro);
    }

    private usuarioAdminDetalleEntidad aDetalle(usuario u) {
        long mascotas = mascotaRepositorio.count(
                "dador.id = ?1 and (estado is null or estado <> 'ELIMINADA')", u.id);
        long postulaciones = postulacionRepositorio.count("adoptante.id", u.id);
        List<moderacionHistorialEntidad> historial = moderacionRepositorio.listarPorUsuario(u.id).stream()
                .map(m -> new moderacionHistorialEntidad(
                        m.fecha, m.accion, m.motivo,
                        ((m.admin.nombre == null ? "" : m.admin.nombre) + " "
                                + (m.admin.apellido == null ? "" : m.admin.apellido)).trim()))
                .toList();
        return new usuarioAdminDetalleEntidad(
                u.id, u.nombre, u.apellido, u.correo, u.rol, u.estado, u.fecha_registro,
                u.telefono, u.direccion,
                fotoPerfilRepositorio.findByUsuarioId(u.id) != null,
                new usuarioAdminDetalleEntidad.Actividad(mascotas, postulaciones),
                historial);
    }
}
