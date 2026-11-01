package ucentral.edu.sadm.mascotas.dominio;

import java.text.Normalizer;
import java.util.Locale;

/** Normaliza texto para comparar sin distinguir mayúsculas ni tildes ("FIRÚ" = "firu"). */
public final class textoNormalizado {

    private textoNormalizado() {
    }

    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        // NFD separa la letra de su tilde ("ú" -> "u" + marca); luego se borran las marcas.
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }
}
