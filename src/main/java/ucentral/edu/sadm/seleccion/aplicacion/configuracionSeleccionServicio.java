package ucentral.edu.sadm.seleccion.aplicacion;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ucentral.edu.sadm.common.infraestructure.BusinessException;
import ucentral.edu.sadm.mascotas.dominio.exception.MascotaNoEncontradaException;
import ucentral.edu.sadm.mascotas.dominio.mascota;
import ucentral.edu.sadm.mascotas.dominio.mascotaRepositorio;
import ucentral.edu.sadm.seleccion.dominio.configuracionSeleccion;
import ucentral.edu.sadm.seleccion.dominio.configuracionSeleccionRepositorio;
import ucentral.edu.sadm.seleccion.dominio.criterioEvaluacionSeleccion;
import ucentral.edu.sadm.seleccion.dominio.documentoSeleccion;
import ucentral.edu.sadm.seleccion.dominio.etapaSeleccion;
import ucentral.edu.sadm.seleccion.dominio.preguntaSeleccion;
import ucentral.edu.sadm.seleccion.dominio.tipoEtapaSeleccion;
import ucentral.edu.sadm.seleccion.infraestructura.dto.configuracionSeleccionEntidad;
import ucentral.edu.sadm.seleccion.infraestructura.dto.configuracionSeleccionSolicitud;
import ucentral.edu.sadm.usuarios.dominio.usuario;
import ucentral.edu.sadm.usuarios.dominio.usuarioRepositorio;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class configuracionSeleccionServicio {

    @Inject
    configuracionSeleccionRepositorio configuracionRepositorio;

    @Inject
    mascotaRepositorio mascotaRepositorio;

    @Inject
    usuarioRepositorio usuarioRepositorio;

    @Transactional
    public configuracionSeleccionEntidad consultar(Long mascotaId, String correoDador) {
        mascota mascota = buscarMascotaPropietario(mascotaId, correoDador);
        configuracionSeleccion configuracion = configuracionRepositorio.buscarPorMascota(mascota.id);
        return convertir(mascota.id, configuracion);
    }

    @Transactional
    public configuracionSeleccionEntidad guardar(
            Long mascotaId,
            String correoDador,
            configuracionSeleccionSolicitud solicitud
    ) {
        mascota mascota = buscarMascotaPropietario(mascotaId, correoDador);
        validarEditable(mascota);

        configuracionSeleccion configuracion = configuracionRepositorio.buscarPorMascota(mascota.id);
        if (configuracion == null) {
            configuracion = new configuracionSeleccion();
            configuracion.mascota = mascota;
            configuracionRepositorio.persist(configuracion);
            for (tipoEtapaSeleccion tipo : tipoEtapaSeleccion.values()) {
                etapaSeleccion etapa = new etapaSeleccion();
                etapa.tipo = tipo;
                etapa.puntajeMaximo = 0;
                configuracion.agregarEtapa(etapa);
            }
        }

        validarIDsUnicos(solicitud);
        Map<tipoEtapaSeleccion, etapaSeleccion> etapas = indexarEtapas(configuracion);

        etapaSeleccion preguntas = etapas.get(tipoEtapaSeleccion.PREGUNTAS);
        preguntas.puntajeMaximo = solicitud.preguntas().puntajeMaximo();
        actualizarPreguntas(preguntas, solicitud.preguntas().elementos());

        etapaSeleccion documentos = etapas.get(tipoEtapaSeleccion.DOCUMENTOS);
        documentos.puntajeMaximo = solicitud.documentos().puntajeMaximo();
        actualizarDocumentos(documentos, solicitud.documentos().elementos());

        etapaSeleccion entrevista = etapas.get(tipoEtapaSeleccion.ENTREVISTA);
        entrevista.puntajeMaximo = solicitud.entrevista().puntajeMaximo();
        actualizarCriterios(entrevista, solicitud.entrevista().criterios());

        etapaSeleccion visita = etapas.get(tipoEtapaSeleccion.VISITA_DOMICILIARIA);
        visita.puntajeMaximo = solicitud.visitaDomiciliaria().puntajeMaximo();
        actualizarCriterios(visita, solicitud.visitaDomiciliaria().criterios());

        validarPuntajes(etapas);
        configuracion.definitiva = false;
        configuracionRepositorio.persistAndFlush(configuracion);
        return convertir(mascota.id, configuracion);
    }

    @Transactional
    public configuracionSeleccionEntidad finalizar(Long mascotaId, String correoDador) {
        mascota mascota = buscarMascotaPropietario(mascotaId, correoDador);
        validarEditable(mascota);
        configuracionSeleccion configuracion = configuracionRepositorio.buscarPorMascota(mascota.id);
        validarCompleta(configuracion);
        configuracion.definitiva = true;
        return convertir(mascota.id, configuracion);
    }

    @Transactional
    public void validarFinalizadaParaPublicar(Long mascotaId, String correoDador) {
        mascota mascota = buscarMascotaPropietario(mascotaId, correoDador);
        configuracionSeleccion configuracion = configuracionRepositorio.buscarPorMascota(mascota.id);
        if (configuracion == null || !configuracion.definitiva) {
            throw errorConfiguracion(
                    409,
                    "CONFIGURACION_NO_FINALIZADA",
                    "Debes finalizar una configuración completa antes de enviar la mascota a revisión"
            );
        }
        validarCompleta(configuracion);
    }

    private mascota buscarMascotaPropietario(Long mascotaId, String correoDador) {
        mascota mascota = mascotaRepositorio.findById(mascotaId);
        if (mascota == null || "ELIMINADA".equals(mascota.estado)) {
            throw new MascotaNoEncontradaException();
        }
        usuario dador = usuarioRepositorio.findByEmail(correoDador);
        if (dador == null || mascota.dador == null || !mascota.dador.id.equals(dador.id)) {
            throw new BusinessException(
                    403,
                    "MASCOTA_NO_AUTORIZADA",
                    "No puedes consultar o modificar la configuración de esta publicación",
                    "Solo el dador propietario puede consultar o modificar la configuración"
            );
        }
        return mascota;
    }

    private void validarEditable(mascota mascota) {
        if (!"BORRADOR_CONFIGURACION".equals(mascota.estado)) {
            throw errorConfiguracion(
                    409,
                    "CONFIGURACION_NO_EDITABLE",
                    "La configuración solo puede modificarse mientras la mascota está en borrador"
            );
        }
    }

    private void validarCompleta(configuracionSeleccion configuracion) {
        if (configuracion == null) {
            throw errorConfiguracion(
                    400,
                    "CONFIGURACION_INCOMPLETA",
                    "Primero debes guardar la configuración de las cuatro etapas"
            );
        }

        Map<tipoEtapaSeleccion, etapaSeleccion> etapas = indexarEtapas(configuracion);
        if (etapas.size() != tipoEtapaSeleccion.values().length) {
            throw errorConfiguracion(
                    400,
                    "CONFIGURACION_INCOMPLETA",
                    "La configuración debe incluir las cuatro etapas"
            );
        }

        for (tipoEtapaSeleccion tipo : tipoEtapaSeleccion.values()) {
            etapaSeleccion etapa = etapas.get(tipo);
            if (etapa == null || cantidadElementos(etapa) == 0) {
                throw errorConfiguracion(
                        400,
                        "ETAPA_SIN_CRITERIOS",
                        "Cada etapa debe tener al menos una pregunta, documento o criterio configurado"
                );
            }
        }

        int total = etapas.values().stream().mapToInt(etapa -> etapa.puntajeMaximo).sum();
        if (total != 100) {
            throw errorConfiguracion(
                    400,
                    "DISTRIBUCION_INVALIDA",
                    "La suma de los puntajes máximos de las cuatro etapas debe ser exactamente 100"
            );
        }
        validarPuntajes(etapas);
    }

    private void validarPuntajes(Map<tipoEtapaSeleccion, etapaSeleccion> etapas) {
        for (etapaSeleccion etapa : etapas.values()) {
            if (etapa.puntajeMaximo == null || etapa.puntajeMaximo < 0) {
                throw errorConfiguracion(400, "PUNTAJE_INVALIDO", "Los puntajes máximos no pueden ser negativos");
            }
            long asignado = sumaPuntajes(etapa);
            if (asignado > etapa.puntajeMaximo) {
                throw errorConfiguracion(
                        400,
                        "PUNTAJE_SUPERA_ETAPA",
                        "La suma de los puntajes de los elementos supera el máximo de su etapa"
                );
            }
        }
    }

    private long sumaPuntajes(etapaSeleccion etapa) {
        return switch (etapa.tipo) {
            case PREGUNTAS -> etapa.preguntas.stream().mapToLong(pregunta -> pregunta.puntaje).sum();
            case DOCUMENTOS -> etapa.documentos.stream().mapToLong(documento -> documento.puntajeMaximo).sum();
            case ENTREVISTA, VISITA_DOMICILIARIA ->
                    etapa.criterios.stream().mapToLong(criterio -> criterio.puntajeMaximo).sum();
        };
    }

    private int cantidadElementos(etapaSeleccion etapa) {
        return switch (etapa.tipo) {
            case PREGUNTAS -> etapa.preguntas.size();
            case DOCUMENTOS -> etapa.documentos.size();
            case ENTREVISTA, VISITA_DOMICILIARIA -> etapa.criterios.size();
        };
    }

    private Map<tipoEtapaSeleccion, etapaSeleccion> indexarEtapas(configuracionSeleccion configuracion) {
        Map<tipoEtapaSeleccion, etapaSeleccion> etapas = new EnumMap<>(tipoEtapaSeleccion.class);
        for (etapaSeleccion etapa : configuracion.etapas) {
            etapas.put(etapa.tipo, etapa);
        }
        return etapas;
    }

    private void actualizarPreguntas(etapaSeleccion etapa, List<configuracionSeleccionSolicitud.preguntaSolicitud> solicitudes) {
        Map<Long, preguntaSeleccion> existentes = new HashMap<>();
        for (preguntaSeleccion pregunta : etapa.preguntas) {
            existentes.put(pregunta.id, pregunta);
        }
        List<preguntaSeleccion> actualizadas = new ArrayList<>();
        for (var solicitud : solicitudes) {
            preguntaSeleccion pregunta = solicitud.id() == null ? new preguntaSeleccion() : existentes.get(solicitud.id());
            validarIDExistente(solicitud.id(), pregunta);
            pregunta.etapa = etapa;
            pregunta.texto = solicitud.texto().trim();
            pregunta.tipoRespuesta = solicitud.tipoRespuesta();
            pregunta.puntaje = solicitud.puntaje();
            pregunta.opciones.clear();
            pregunta.opciones.addAll(solicitud.opciones());
            if (pregunta.tipoRespuesta == ucentral.edu.sadm.seleccion.dominio.tipoRespuestaSeleccion.SELECCION_UNICA
                    || pregunta.tipoRespuesta == ucentral.edu.sadm.seleccion.dominio.tipoRespuestaSeleccion.SELECCION_MULTIPLE) {
                Set<String> opcionesNormalizadas = new HashSet<>();
                for (String opcion : pregunta.opciones) {
                    if (!opcionesNormalizadas.add(opcion.trim().toLowerCase(Locale.ROOT))) {
                        throw errorConfiguracion(400, "OPCIONES_DUPLICADAS", "Las opciones de una pregunta no pueden repetirse");
                    }
                }
            }
            actualizadas.add(pregunta);
        }
        etapa.preguntas.clear();
        etapa.preguntas.addAll(actualizadas);
    }

    private void actualizarDocumentos(etapaSeleccion etapa, List<configuracionSeleccionSolicitud.documentoSolicitud> solicitudes) {
        Map<Long, documentoSeleccion> existentes = new HashMap<>();
        for (documentoSeleccion documento : etapa.documentos) {
            existentes.put(documento.id, documento);
        }
        List<documentoSeleccion> actualizados = new ArrayList<>();
        for (var solicitud : solicitudes) {
            documentoSeleccion documento = solicitud.id() == null ? new documentoSeleccion() : existentes.get(solicitud.id());
            validarIDExistente(solicitud.id(), documento);
            documento.etapa = etapa;
            documento.nombre = solicitud.nombre().trim();
            documento.descripcion = solicitud.descripcion() == null ? null : solicitud.descripcion().trim();
            documento.obligatorio = solicitud.obligatorio();
            documento.puntajeMaximo = solicitud.puntajeMaximo();
            actualizados.add(documento);
        }
        etapa.documentos.clear();
        etapa.documentos.addAll(actualizados);
    }

    private void actualizarCriterios(etapaSeleccion etapa, List<configuracionSeleccionSolicitud.criterioSolicitud> solicitudes) {
        Map<Long, criterioEvaluacionSeleccion> existentes = new HashMap<>();
        for (criterioEvaluacionSeleccion criterio : etapa.criterios) {
            existentes.put(criterio.id, criterio);
        }
        List<criterioEvaluacionSeleccion> actualizados = new ArrayList<>();
        for (var solicitud : solicitudes) {
            criterioEvaluacionSeleccion criterio = solicitud.id() == null
                    ? new criterioEvaluacionSeleccion()
                    : existentes.get(solicitud.id());
            validarIDExistente(solicitud.id(), criterio);
            criterio.etapa = etapa;
            criterio.descripcion = solicitud.descripcion().trim();
            criterio.puntajeMaximo = solicitud.puntajeMaximo();
            actualizados.add(criterio);
        }
        etapa.criterios.clear();
        etapa.criterios.addAll(actualizados);
    }

    private void validarIDsUnicos(configuracionSeleccionSolicitud solicitud) {
        validarIDsUnicos(solicitud.preguntas().elementos().stream()
                .map(configuracionSeleccionSolicitud.preguntaSolicitud::id).toList());
        validarIDsUnicos(solicitud.documentos().elementos().stream()
                .map(configuracionSeleccionSolicitud.documentoSolicitud::id).toList());
        List<Long> criterios = new ArrayList<>();
        solicitud.entrevista().criterios().stream().map(configuracionSeleccionSolicitud.criterioSolicitud::id)
                .filter(id -> id != null).forEach(criterios::add);
        solicitud.visitaDomiciliaria().criterios().stream().map(configuracionSeleccionSolicitud.criterioSolicitud::id)
                .filter(id -> id != null).forEach(criterios::add);
        validarIDsUnicos(criterios);
    }

    private void validarIDsUnicos(List<Long> ids) {
        Set<Long> unicos = new HashSet<>();
        for (Long id : ids) {
            if (id != null && !unicos.add(id)) {
                throw errorConfiguracion(400, "ID_DUPLICADO", "No se puede incluir el mismo elemento más de una vez");
            }
        }
    }

    private void validarIDExistente(Long id, Object entidad) {
        if (id != null && entidad == null) {
            throw errorConfiguracion(
                    400,
                    "ELEMENTO_NO_PERTENECE_ETAPA",
                    "El identificador no pertenece a esta etapa de la configuración"
            );
        }
    }

    private configuracionSeleccionEntidad convertir(Long mascotaId, configuracionSeleccion configuracion) {
        Map<tipoEtapaSeleccion, etapaSeleccion> etapas = configuracion == null
                ? Map.of()
                : indexarEtapas(configuracion);
        int distribuido = etapas.values().stream().mapToInt(etapa -> etapa.puntajeMaximo).sum();

        etapaSeleccion preguntas = etapas.get(tipoEtapaSeleccion.PREGUNTAS);
        etapaSeleccion documentos = etapas.get(tipoEtapaSeleccion.DOCUMENTOS);
        etapaSeleccion entrevista = etapas.get(tipoEtapaSeleccion.ENTREVISTA);
        etapaSeleccion visita = etapas.get(tipoEtapaSeleccion.VISITA_DOMICILIARIA);

        return new configuracionSeleccionEntidad(
                mascotaId,
                configuracion != null && configuracion.definitiva,
                distribuido,
                Math.max(100 - distribuido, 0),
                Math.max(distribuido - 100, 0),
                convertirPreguntas(preguntas),
                convertirDocumentos(documentos),
                convertirEvaluacion(entrevista),
                convertirEvaluacion(visita)
        );
    }

    private configuracionSeleccionEntidad.etapaPreguntas convertirPreguntas(etapaSeleccion etapa) {
        return new configuracionSeleccionEntidad.etapaPreguntas(
                etapa == null ? 0 : etapa.puntajeMaximo,
                etapa == null ? List.of() : etapa.preguntas.stream()
                        .map(pregunta -> new configuracionSeleccionEntidad.pregunta(
                                pregunta.id, pregunta.texto, pregunta.tipoRespuesta,
                                pregunta.puntaje, List.copyOf(pregunta.opciones)))
                        .toList()
        );
    }

    private configuracionSeleccionEntidad.etapaDocumentos convertirDocumentos(etapaSeleccion etapa) {
        return new configuracionSeleccionEntidad.etapaDocumentos(
                etapa == null ? 0 : etapa.puntajeMaximo,
                etapa == null ? List.of() : etapa.documentos.stream()
                        .map(documento -> new configuracionSeleccionEntidad.documento(
                                documento.id, documento.nombre, documento.descripcion,
                                documento.obligatorio, documento.puntajeMaximo))
                        .toList()
        );
    }

    private configuracionSeleccionEntidad.etapaEvaluacion convertirEvaluacion(etapaSeleccion etapa) {
        return new configuracionSeleccionEntidad.etapaEvaluacion(
                etapa == null ? 0 : etapa.puntajeMaximo,
                etapa == null ? List.of() : etapa.criterios.stream()
                        .map(criterio -> new configuracionSeleccionEntidad.criterio(
                                criterio.id, criterio.descripcion, criterio.puntajeMaximo))
                        .toList()
        );
    }

    private BusinessException errorConfiguracion(int status, String codigo, String mensaje) {
        return new BusinessException(status, codigo, mensaje, mensaje);
    }
}
