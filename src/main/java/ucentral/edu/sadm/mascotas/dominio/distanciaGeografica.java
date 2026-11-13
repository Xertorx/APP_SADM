package ucentral.edu.sadm.mascotas.dominio;

/**
 * HU10: utilidades puras de geolocalización (sin acceso a BD).
 * La distancia se calcula con la fórmula de Haversine (círculo máximo sobre una esfera).
 */
public final class distanciaGeografica {

    private static final double RADIO_TIERRA_KM = 6371.0088;

    private distanciaGeografica() {
    }

    /** Distancia en km entre dos puntos, redondeada a 1 decimal. */
    public static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double km = RADIO_TIERRA_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(km * 10.0) / 10.0;
    }

    /** Redondea a 3 decimales (~100 m): privacidad, porque las coordenadas de una mascota son la casa del dador. */
    public static Double redondear(Double valor) {
        return valor == null ? null : Math.round(valor * 1000.0) / 1000.0;
    }

    public static boolean latitudValida(double lat) {
        return lat >= -90 && lat <= 90;
    }

    public static boolean longitudValida(double lon) {
        return lon >= -180 && lon <= 180;
    }
}
