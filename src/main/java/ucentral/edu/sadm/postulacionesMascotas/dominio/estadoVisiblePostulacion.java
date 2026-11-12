package ucentral.edu.sadm.postulacionesMascotas.dominio;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HU21: estado que ve el adoptante (Activa / Ganadora / Descartada).
 * El mapeo desde estadoPostulacion vive SOLO aquí: si Juan agrega estados
 * (ETAPA_2_..., ETAPA_3_..., rechazos), se ajusta en un único lugar.
 */
public enum estadoVisiblePostulacion {
    ACTIVA("Activa"),
    GANADORA("Ganadora"),
    DESCARTADA("Descartada");

    public static final int TOTAL_ETAPAS = 4;

    private static final Pattern ETAPA = Pattern.compile("^ETAPA_(\\d+)_");

    private final String etiqueta;

    estadoVisiblePostulacion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /** Estado visible a partir del estado interno de la postulación. */
    public static estadoVisiblePostulacion desde(estadoPostulacion estado) {
        if (estado == null) {
            return DESCARTADA;
        }
        // TODO HU20: cuando exista el estado GANADORA devolver GANADORA aquí.
        if (ETAPA.matcher(estado.name()).find()) {
            return ACTIVA;
        }
        // CANCELADA_POR_DADOR y cualquier estado de rechazo/retiro.
        return DESCARTADA;
    }

    /** Etapa (1 a TOTAL_ETAPAS) de la postulación. */
    public static int etapaDe(estadoPostulacion estado) {
        if (estado == null) {
            return 1;
        }
        // TODO HU20: una postulación ganadora estará en la etapa TOTAL_ETAPAS.
        Matcher m = ETAPA.matcher(estado.name());
        if (m.find()) {
            int n = Integer.parseInt(m.group(1));
            return Math.max(1, Math.min(TOTAL_ETAPAS, n));
        }
        // TODO: cuando existan estados de rechazo por etapa, devolver la etapa real.
        return 1;
    }
}
