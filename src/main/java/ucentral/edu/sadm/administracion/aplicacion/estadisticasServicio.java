package ucentral.edu.sadm.administracion.aplicacion;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.TypedQuery;
import ucentral.edu.sadm.administracion.infraestructura.dto.cifraDisponibleEntidad;
import ucentral.edu.sadm.administracion.infraestructura.dto.estadisticasEntidad;
import ucentral.edu.sadm.common.infraestructure.BusinessException;
import ucentral.edu.sadm.mascotas.dominio.mascotaRepositorio;
import ucentral.edu.sadm.postulacionesMascotas.dominio.estadoPostulacion;
import ucentral.edu.sadm.postulacionesMascotas.dominio.postulacionRepositorio;
import ucentral.edu.sadm.usuarios.dominio.rolUsuario;
import ucentral.edu.sadm.usuarios.dominio.usuarioRepositorio;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * HU30: estadísticas generales (solo lectura, solo ADMINISTRADOR).
 * Todo se calcula con consultas count / group by: la BD devuelve pocas filas y no se cargan
 * entidades (ni colecciones lazy). No se crean tablas ni se modifican entidades.
 * El recurso JSON y el CSV usan el mismo método {@link #generar}, así las cifras coinciden.
 */
@ApplicationScoped
public class estadisticasServicio {

    static final String MENSAJE_RANGO = "La fecha inicial no puede ser posterior a la final";
    static final String MENSAJE_FORMATO = "Formato de fecha inválido (use yyyy-MM-dd)";
    static final String SIN_ESTADO = "SIN_ESTADO";
    static final String NOTA_ESTADO_ACTUAL = "Estado actual";
    static final String NOTA_HU20 = "Disponible cuando se implemente la notificación al ganador";
    static final String NOTA_HU22 = "Disponible cuando se implemente el seguimiento post-adopción";

    /** Mascotas "publicadas": las que ya salieron del borrador y no están eliminadas (como listarTodas). */
    private static final String PUBLICADAS =
            "(m.estado is null or m.estado not in ('BORRADOR_CONFIGURACION','ELIMINADA'))";

    private static final DateTimeFormatter ISO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter FECHA_ES = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA_ES = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Inject
    mascotaRepositorio mascotaRepositorio;
    @Inject
    usuarioRepositorio usuarioRepositorio;
    @Inject
    postulacionRepositorio postulacionRepositorio;

    /** desde / hasta opcionales (yyyy-MM-dd). Sin ellos = histórico. */
    public estadisticasEntidad generar(String desdeTexto, String hastaTexto) {
        LocalDate desde = parsear(desdeTexto);
        LocalDate hasta = parsear(hastaTexto);
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new BusinessException(400, MENSAJE_RANGO);
        }

        Map<String, Long> porEstado = mascotasPorEstado(desde, hasta);
        long publicadas = porEstado.values().stream().mapToLong(Long::longValue).sum();

        return new estadisticasEntidad(
                publicadas,
                porEstado,
                // TODO HU20: cuando exista el estado ADOPTADA / ganador, contar aquí (disponible = true).
                new cifraDisponibleEntidad(0, false),
                usuariosActivosPorRol(),
                postulacionesEnCurso(),
                // TODO HU22: cuando exista el seguimiento post-adopción, contar aquí (disponible = true).
                new cifraDisponibleEntidad(0, false),
                desde == null ? null : desde.toString(),
                hasta == null ? null : hasta.toString(),
                LocalDateTime.now().format(ISO_FECHA_HORA));
    }

    /** CSV para Excel en español: BOM UTF-8 + separador ';'. */
    public byte[] exportarCsv(String desde, String hasta) {
        estadisticasEntidad e = generar(desde, hasta);
        LocalDateTime generado = LocalDateTime.parse(e.generadoEn(), ISO_FECHA_HORA);

        List<String> filas = new ArrayList<>();
        filas.add(fila("Reporte de estadísticas", "SADM"));
        filas.add(fila("Generado el", generado.format(FECHA_HORA_ES)));
        filas.add(fila("Desde", e.desde() == null ? "Sin filtro" : LocalDate.parse(e.desde()).format(FECHA_ES)));
        filas.add(fila("Hasta", e.hasta() == null ? "Sin filtro" : LocalDate.parse(e.hasta()).format(FECHA_ES)));
        filas.add(fila("Indicador", "Valor", "Nota"));
        filas.add(fila("Mascotas publicadas", e.mascotasPublicadas(), ""));
        e.mascotasPorEstado().forEach((estado, n) -> filas.add(fila("Mascotas - " + estado, n, "")));
        filas.add(fila("Adopciones completadas", e.adopcionesCompletadas().valor(),
                e.adopcionesCompletadas().disponible() ? "" : NOTA_HU20));
        e.usuariosActivosPorRol().forEach((rol, n) ->
                filas.add(fila("Usuarios activos - " + rol, n, NOTA_ESTADO_ACTUAL)));
        filas.add(fila("Postulaciones en curso", e.postulacionesEnCurso(), NOTA_ESTADO_ACTUAL));
        filas.add(fila("Adopciones en seguimiento", e.adopcionesEnSeguimiento().valor(),
                e.adopcionesEnSeguimiento().disponible() ? NOTA_ESTADO_ACTUAL : NOTA_HU22));

        String contenido = "﻿" + String.join("\r\n", filas) + "\r\n";
        return contenido.getBytes(StandardCharsets.UTF_8);
    }

    /** Nombre del archivo: reporte_estadisticas_yyyyMMdd.csv */
    public String nombreArchivoCsv() {
        return "reporte_estadisticas_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".csv";
    }

    // ---------- consultas ----------

    /** Publicadas por estado, filtradas por fecha_publicacion si hay rango (con fecha null quedan fuera). */
    private Map<String, Long> mascotasPorEstado(LocalDate desde, LocalDate hasta) {
        StringBuilder jpql = new StringBuilder("select m.estado, count(m) from mascota m where " + PUBLICADAS);
        List<Object> params = new ArrayList<>();
        if (desde != null) {
            params.add(inicioDelDia(desde));
            jpql.append(" and m.fecha_publicacion >= ?").append(params.size());
        }
        if (hasta != null) {
            // "hasta" incluye todo ese día: < inicio del día siguiente.
            params.add(inicioDelDia(hasta.plusDays(1)));
            jpql.append(" and m.fecha_publicacion < ?").append(params.size());
        }
        jpql.append(" group by m.estado order by m.estado");

        TypedQuery<Object[]> consulta = mascotaRepositorio.getEntityManager()
                .createQuery(jpql.toString(), Object[].class);
        for (int i = 0; i < params.size(); i++) {
            consulta.setParameter(i + 1, params.get(i));
        }
        Map<String, Long> resultado = new LinkedHashMap<>();
        for (Object[] fila : consulta.getResultList()) {
            String estado = fila[0] == null ? SIN_ESTADO : (String) fila[0];
            resultado.merge(estado, (Long) fila[1], Long::sum);
        }
        return resultado;
    }

    /** Usuarios ACTIVO por rol (estado actual). Los 3 roles siempre aparecen, con 0 si no hay. */
    private Map<String, Long> usuariosActivosPorRol() {
        List<Object[]> filas = usuarioRepositorio.getEntityManager()
                .createQuery("select u.rol, count(u) from usuario u where u.estado = 'ACTIVO' group by u.rol",
                        Object[].class)
                .getResultList();
        Map<String, Long> resultado = new LinkedHashMap<>();
        for (rolUsuario rol : rolUsuario.values()) {
            resultado.put(rol.name(), 0L);
        }
        for (Object[] fila : filas) {
            resultado.put(((rolUsuario) fila[0]).name(), (Long) fila[1]);
        }
        return resultado;
    }

    /**
     * Postulaciones en etapa activa (estado actual): los estados ETAPA_n_*, misma regla que
     * estadoVisiblePostulacion.ACTIVA. Se calculan desde el enum para que ETAPA_2… futuras cuenten solas.
     */
    private long postulacionesEnCurso() {
        List<estadoPostulacion> enCurso = Arrays.stream(estadoPostulacion.values())
                .filter(s -> s.name().startsWith("ETAPA_"))
                .toList();
        if (enCurso.isEmpty()) {
            return 0;
        }
        return postulacionRepositorio.count("estado in ?1", enCurso);
    }

    // ---------- utilidades ----------

    private static LocalDate parsear(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            throw new BusinessException(400, MENSAJE_FORMATO);
        }
    }

    private static Date inicioDelDia(LocalDate fecha) {
        return Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /** Une celdas con ';' escapando comillas, ';' y saltos de línea. */
    private static String fila(Object... celdas) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < celdas.length; i++) {
            if (i > 0) {
                sb.append(';');
            }
            sb.append(celda(String.valueOf(celdas[i])));
        }
        return sb.toString();
    }

    private static String celda(String valor) {
        if (valor.contains(";") || valor.contains("\"") || valor.contains("\n") || valor.contains("\r")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
}
