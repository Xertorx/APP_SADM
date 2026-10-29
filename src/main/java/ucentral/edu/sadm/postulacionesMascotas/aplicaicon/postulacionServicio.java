package ucentral.edu.sadm.postulacionesMascotas.aplicaicon;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import ucentral.edu.sadm.usuarios.dominio.usuario;
import ucentral.edu.sadm.usuarios.dominio.usuarioRepositorio;
import ucentral.edu.sadm.mascotas.dominio.fotoMascotaRepositorio;
import ucentral.edu.sadm.mascotas.dominio.mascota;
import ucentral.edu.sadm.mascotas.dominio.mascotaRepositorio;
import ucentral.edu.sadm.postulacionesMascotas.dominio.estadoPostulacion;
import ucentral.edu.sadm.postulacionesMascotas.dominio.postulacion;
import ucentral.edu.sadm.postulacionesMascotas.dominio.postulacionRepositorio;
import ucentral.edu.sadm.postulacionesMascotas.dominio.exception.PostulacionException;
import ucentral.edu.sadm.postulacionesMascotas.infraestructura.dto.postulacionEntidad;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class postulacionServicio {
    private static final String UNIQUE_CONSTRAINT = "uk_postulacion_adoptante_mascota";
    private static final List<postulacionEntidad.EtapaProceso> ETAPAS = List.of(
            new postulacionEntidad.EtapaProceso("Revisión inicial", "Validación de los datos y requisitos del adoptante."),
            new postulacionEntidad.EtapaProceso("Evaluación del hogar", "Revisión de las condiciones para el bienestar de la mascota."),
            new postulacionEntidad.EtapaProceso("Entrevista", "Conversación con el adoptante para conocer su motivación y experiencia."),
            new postulacionEntidad.EtapaProceso("Decisión de adopción", "Comunicación de la decisión final del proceso.")
    );

    @Inject
    mascotaRepositorio mascotaRepositorio;

    @Inject
    usuarioRepositorio usuarioRepositorio;

    @Inject
    fotoMascotaRepositorio fotoMascotaRepositorio;

    @Inject
    postulacionRepositorio postulacionRepositorio;

    public postulacionEntidad resumen(Long mascotaId, Long adoptanteId) {
        mascota mascota = buscarMascota(mascotaId);
        String foto = fotoMascotaRepositorio.listarPorMascota(mascotaId).stream()
                .findFirst()
                .map(f -> "/SADM/mascotas/fotos/" + f.id)
                .orElse(null);

        postulacion existente = postulacionRepositorio.buscarPorAdoptanteYMascota(adoptanteId, mascotaId);
        boolean postulacionActiva = existente != null;
        boolean disponible = estaDisponible(mascota);
        String motivo = null;
        String mensaje;
        if (postulacionActiva) {
            motivo = "POSTULACION_DUPLICADA";
            mensaje = "Ya tienes una postulación activa para esta mascota";
        } else if (!disponible) {
            motivo = "MASCOTA_NO_DISPONIBLE";
            mensaje = "Esta mascota no está disponible para postulación";
        } else {
            mensaje = "Puedes postularte a esta mascota";
        }

        return postulacionEntidad.resumen(
                new postulacionEntidad.MascotaResumen(mascota.id, mascota.nombre, foto, mascota.especie, mascota.raza),
                ETAPAS,
                disponible && !postulacionActiva,
                motivo,
                postulacionActiva,
                postulacionActiva ? existente.estado.name() : null,
                mensaje
        );
    }

    @Transactional
    public postulacionEntidad crear(Long adoptanteId, Long mascotaId) {
        mascota mascota = buscarMascota(mascotaId);
        validarDisponibilidad(mascota);
        validarNoDuplicada(adoptanteId, mascotaId);
        usuario adoptante = usuarioRepositorio.findById(adoptanteId);

        postulacion postulacion = new postulacion();
        postulacion.adoptante = adoptante;
        postulacion.mascota = mascota;
        postulacion.fecha_postulacion = Instant.now();
        postulacion.estado = estadoPostulacion.ETAPA_1_EN_CURSO;
        postulacion.puntaje = 0;

        try {
            postulacionRepositorio.persistAndFlush(postulacion);
        } catch (RuntimeException exception) {
            if (esRestriccionDuplicada(exception)) {
                throw duplicada();
            }
            throw exception;
        }

        return convertir(postulacion);
    }

    private mascota buscarMascota(Long mascotaId) {
        mascota mascota = mascotaRepositorio.findById(mascotaId);
        if (mascota == null) {
            throw new PostulacionException(
                    404,
                    "MASCOTA_NO_ENCONTRADA",
                    "La mascota no existe",
                    "No se encontró una mascota con el identificador solicitado"
            );
        }
        return mascota;
    }

    private void validarDisponibilidad(mascota mascota) {
        if (!estaDisponible(mascota)) {
            throw new PostulacionException(
                    409,
                    "MASCOTA_NO_DISPONIBLE",
                    "La mascota no está disponible para postulación",
                    "Solo se aceptan postulaciones para mascotas en estado Disponible"
            );
        }
    }

    private void validarNoDuplicada(Long adoptanteId, Long mascotaId) {
        if (postulacionRepositorio.buscarPorAdoptanteYMascota(adoptanteId, mascotaId) != null) {
            throw duplicada();
        }
    }

    private PostulacionException duplicada() {
        return new PostulacionException(
                409,
                "POSTULACION_DUPLICADA",
                "Ya existe una postulación de este adoptante para la mascota",
                "Un adoptante solo puede postularse una vez a cada mascota"
        );
    }

    private boolean estaDisponible(mascota mascota) {
        return mascota.estado != null && "DISPONIBLE".equalsIgnoreCase(mascota.estado.trim());
    }

    private boolean esRestriccionDuplicada(Throwable exception) {
        Throwable actual = exception;
        while (actual != null) {
            if (actual instanceof ConstraintViolationException violation) {
                String constraintName = violation.getConstraintName();
                if (constraintName != null
                        && constraintName.toLowerCase(Locale.ROOT).contains(UNIQUE_CONSTRAINT)) {
                    return true;
                }
            }
            if (actual instanceof SQLException sqlException
                    && "23505".equals(sqlException.getSQLState())
                    && sqlException.getMessage() != null
                    && sqlException.getMessage().toLowerCase(Locale.ROOT).contains(UNIQUE_CONSTRAINT)) {
                return true;
            }
            actual = actual.getCause();
        }
        return false;
    }

    private postulacionEntidad convertir(postulacion postulacion) {
        return postulacionEntidad.creada(
                postulacion.id, postulacion.mascota.id, postulacion.adoptante.id,
                postulacion.fecha_postulacion,
                postulacion.estado.name(),
                postulacion.puntaje
        );
    }
}
