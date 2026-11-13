package ucentral.edu.sadm.mascotas.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class mascotaRepositorio implements PanacheRepository<mascota> {

    public List<mascota> listarTodas() {
        return list("estado is null or estado not in ('ELIMINADA', 'BORRADOR_CONFIGURACION')");
    }

    public List<mascota> listarPorDador(Long dadorId) {
        return list("dador.id = ?1 and (estado is null or estado <> 'ELIMINADA')", dadorId);
    }

    // HU08-HU09: solo DISPONIBLES; especie y tamaño se filtran aquí si la lista no está vacía.
    public List<mascota> listarDisponibles(List<String> especies, List<String> tamanos) {
        // HU27-HU28: se ocultan las mascotas de dadores INHABILITADOS (mascota.estado no cambia, así que
        // al reactivar vuelven solas). Se usa subconsulta por id: "dador.estado" metería un inner join
        // implícito y descartaría las mascotas antiguas sin dador.
        StringBuilder consulta = new StringBuilder("estado = 'DISPONIBLE' and (dador is null or dador.id in "
                + "(select u.id from usuario u where u.estado = 'ACTIVO'))");
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
