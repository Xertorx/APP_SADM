package ucentral.edu.sadm.postulacionesMascotas.aplicaicon;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ucentral.edu.sadm.mascotas.dominio.fotoMascotaRepositorio;
import ucentral.edu.sadm.postulacionesMascotas.dominio.estadoVisiblePostulacion;
import ucentral.edu.sadm.postulacionesMascotas.dominio.postulacion;
import ucentral.edu.sadm.postulacionesMascotas.dominio.postulacionRepositorio;
import ucentral.edu.sadm.postulacionesMascotas.infraestructura.dto.miPostulacionEntidad;
import ucentral.edu.sadm.usuarios.dominio.usuario;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * HU21: consulta de "Mis postulaciones" del adoptante autenticado.
 * Sin @Transactional a propósito: es solo lectura y la consulta trae mascota y dador
 * con join fetch, así no se tocan relaciones lazy fuera de una sesión.
 */
@ApplicationScoped
public class misPostulacionesServicio {

    @Inject
    postulacionRepositorio postulacionRepositorio;

    @Inject
    fotoMascotaRepositorio fotoMascotaRepositorio;

    public List<miPostulacionEntidad> listar(Long adoptanteId) {
        List<postulacion> postulaciones = postulacionRepositorio.listarPorAdoptante(adoptanteId);
        if (postulaciones.isEmpty()) {
            return List.of();
        }
        Set<Long> mascotaIds = postulaciones.stream()
                .map(p -> p.mascota.id)
                .collect(Collectors.toSet());
        Map<Long, Long> primerasFotos = primerasFotos(mascotaIds);
        return postulaciones.stream()
                .map(p -> convertir(p, primerasFotos.get(p.mascota.id)))
                .toList();
    }

    /**
     * Id de la primera foto (menor "orden") de cada mascota, en UNA consulta y sin
     * cargar la columna datos (bytea), que pesa mucho.
     */
    private Map<Long, Long> primerasFotos(Set<Long> mascotaIds) {
        List<Object[]> filas = fotoMascotaRepositorio.getEntityManager()
                .createQuery(
                        "select f.mascota.id, f.id from fotoMascota f "
                                + "where f.mascota.id in :ids order by f.orden asc, f.id asc",
                        Object[].class)
                .setParameter("ids", mascotaIds)
                .getResultList();
        Map<Long, Long> resultado = new HashMap<>();
        for (Object[] fila : filas) {
            resultado.putIfAbsent((Long) fila[0], (Long) fila[1]);
        }
        return resultado;
    }

    private miPostulacionEntidad convertir(postulacion p, Long primeraFotoId) {
        estadoVisiblePostulacion visible = estadoVisiblePostulacion.desde(p.estado);
        return new miPostulacionEntidad(
                p.id,
                p.fecha_postulacion,
                p.mascota.id,
                p.mascota.nombre,
                primeraFotoId,
                nombreDador(p.mascota.dador),
                // TODO HU12: usar el nombre del proceso configurado por el dador.
                "Proceso de adopción de " + p.mascota.nombre,
                estadoVisiblePostulacion.etapaDe(p.estado),
                estadoVisiblePostulacion.TOTAL_ETAPAS,
                p.puntaje == null ? 0 : p.puntaje,
                visible.name(),
                visible.getEtiqueta(),
                p.estado == null ? null : p.estado.getEtiqueta(),
                p.updated_at
        );
    }

    /** Solo nombre y apellido: nunca correo ni teléfono. */
    private String nombreDador(usuario dador) {
        if (dador == null) {
            return null;
        }
        String nombre = dador.nombre == null ? "" : dador.nombre.trim();
        String apellido = dador.apellido == null ? "" : dador.apellido.trim();
        return (nombre + " " + apellido).trim();
    }
}
