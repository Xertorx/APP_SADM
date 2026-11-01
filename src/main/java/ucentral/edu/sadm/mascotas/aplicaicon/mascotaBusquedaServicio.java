package ucentral.edu.sadm.mascotas.aplicaicon;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ucentral.edu.sadm.common.infraestructure.BusinessException;
import ucentral.edu.sadm.mascotas.dominio.mascota;
import ucentral.edu.sadm.mascotas.dominio.mascotaRepositorio;
import ucentral.edu.sadm.mascotas.dominio.rangoEdad;
import ucentral.edu.sadm.mascotas.dominio.textoNormalizado;
import ucentral.edu.sadm.mascotas.infraestructura.dto.mascotaDetalleEntidad;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * HU08 + HU09: búsqueda por nombre y filtros sobre mascotas DISPONIBLES.
 * Entre filtros distintos la lógica es «Y»; dentro de uno de selección múltiple es «O»
 * (salvo temperamento, que exige todos los marcados).
 */
// Sin @Transactional a propósito: solo lee, y mascota.temperamento es lazy. Con transacción la sesión
// se cierra al salir del método y Jackson fallaría al serializarla (igual que consultarTodas).
@ApplicationScoped
public class mascotaBusquedaServicio {

    private static final Set<String> ESPECIES = Set.of("PERRO", "GATO", "OTRO");
    private static final Set<String> TAMANOS = Set.of("PEQUENO", "MEDIANO", "GRANDE");
    private static final Set<String> TEMPERAMENTOS = Set.of(
            "JUGUETON", "TRANQUILO", "TIMIDO", "SOCIABLE", "PROTECTOR", "ENERGICO", "CARINOSO");

    @Inject
    mascotaRepositorio mascotaRepositorio;
    @Inject
    mascotaServicio mascotaServicio;

    public List<mascotaDetalleEntidad> buscar(
            String nombre,
            List<String> especies,
            String raza,
            List<String> edades,
            List<String> tamanos,
            List<String> temperamentos
    ) {
        List<String> especiesValidas = validar(especies, ESPECIES, "La especie debe ser PERRO, GATO u OTRO");
        List<String> tamanosValidos = validar(tamanos, TAMANOS, "El tamaño debe ser PEQUENO, MEDIANO o GRANDE");
        List<String> temperamentosValidos = validar(temperamentos, TEMPERAMENTOS, "Selecciona un temperamento válido");
        List<rangoEdad> rangos = validarEdades(edades);

        String nombreBuscado = textoNormalizado.normalizar(nombre);
        String razaBuscada = textoNormalizado.normalizar(raza);

        // Estado, especie y tamaño van en la consulta; el resto se filtra en Java
        // porque la BD compartida no tiene la extensión unaccent.
        return mascotaRepositorio.listarDisponibles(especiesValidas, tamanosValidos).stream()
                .filter(m -> nombreBuscado.isEmpty() || textoNormalizado.normalizar(m.nombre).contains(nombreBuscado))
                .filter(m -> razaBuscada.isEmpty() || textoNormalizado.normalizar(m.raza).equals(razaBuscada))
                .filter(m -> rangos.isEmpty() || coincideEdad(m, rangos))
                .filter(m -> temperamentosValidos.isEmpty()
                        || (m.temperamento != null && m.temperamento.containsAll(temperamentosValidos)))
                .sorted(Comparator.comparing((mascota m) -> m.fecha_publicacion,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(mascotaServicio::convertirDetalle)
                .toList();
    }

    /** Razas distintas (ignorando mayúsculas/tildes) de las mascotas DISPONIBLES de una especie. */
    public List<String> razasPorEspecie(String especie) {
        if (especie == null || especie.isBlank()) {
            return List.of();
        }
        List<String> especiesValidas = validar(List.of(especie), ESPECIES, "La especie debe ser PERRO, GATO u OTRO");

        Map<String, String> porClave = new LinkedHashMap<>();
        for (mascota m : mascotaRepositorio.listarDisponibles(especiesValidas, List.of())) {
            String clave = textoNormalizado.normalizar(m.raza);
            if (!clave.isEmpty()) {
                porClave.putIfAbsent(clave, m.raza.trim());
            }
        }
        List<String> razas = new ArrayList<>();
        porClave.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> razas.add(e.getValue()));
        return razas;
    }

    private boolean coincideEdad(mascota m, List<rangoEdad> rangos) {
        double anios = rangoEdad.aAnios(m.edad, m.unidad_edad);
        return rangos.stream().anyMatch(r -> r.contiene(anios));
    }

    private List<String> validar(List<String> valores, Set<String> permitidos, String mensaje) {
        if (valores == null) {
            return List.of();
        }
        List<String> limpios = valores.stream()
                .filter(v -> v != null && !v.isBlank())
                .map(v -> v.trim().toUpperCase())
                .distinct()
                .toList();
        for (String valor : limpios) {
            if (!permitidos.contains(valor)) {
                throw new BusinessException(400, mensaje);
            }
        }
        return limpios;
    }

    private List<rangoEdad> validarEdades(List<String> edades) {
        List<rangoEdad> rangos = new ArrayList<>();
        if (edades == null) {
            return rangos;
        }
        for (String edad : edades) {
            if (edad == null || edad.isBlank()) {
                continue;
            }
            try {
                rangos.add(rangoEdad.valueOf(edad.trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException(400, "La edad debe ser CACHORRO, JOVEN, ADULTO o SENIOR");
            }
        }
        return rangos;
    }
}
