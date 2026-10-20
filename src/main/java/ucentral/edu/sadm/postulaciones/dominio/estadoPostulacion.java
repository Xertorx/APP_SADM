package ucentral.edu.sadm.postulaciones.dominio;

public enum estadoPostulacion {
    ETAPA_1_EN_CURSO("Etapa 1 en curso");

    private final String etiqueta;

    estadoPostulacion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
