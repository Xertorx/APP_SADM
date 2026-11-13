package ucentral.edu.sadm.seleccion.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class configuracionSeleccionRepositorio implements PanacheRepository<configuracionSeleccion> {

    public configuracionSeleccion buscarPorMascota(Long mascotaId) {
        return find("mascota.id", mascotaId).firstResult();
    }
}
