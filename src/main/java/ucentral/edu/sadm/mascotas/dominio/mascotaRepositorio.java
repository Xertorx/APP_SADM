package ucentral.edu.sadm.mascotas.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class mascotaRepositorio implements PanacheRepository<mascota> {

    public List<mascota> listarTodas() {
        return list("estado is null or estado <> 'ELIMINADA'");
    }

    public List<mascota> listarPorDador(Long dadorId) {
        return list("dador.id = ?1 and (estado is null or estado <> 'ELIMINADA')", dadorId);
    }

    // HU08-HU09: solo DISPONIBLES; especie y tamaño se filtran aquí si la lista no está vacía.
    public List<mascota> listarDisponibles(List<String> especies, List<String> tamanos) {
        StringBuilder consulta = new StringBuilder("estado = 'DISPONIBLE'");
        List<Object> params = new ArrayList<>();
        if (especies != null && !especies.isEmpty()) {
            params.add(especies);
            consulta.append(" and especie in ?").append(params.size());
        }
        if (tamanos != null && !tamanos.isEmpty()) {
            params.add(tamanos);
            consulta.append(" and tamano in ?").append(params.size());
        }
        return list(consulta.toString(), params.toArray());
    }
}
