package ucentral.edu.sadm.postulaciones.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class postulacionRepositorio implements PanacheRepository<postulacion> {

    public postulacion buscarPorAdoptanteYMascota(Long adoptanteId, Long mascotaId) {
        return find("adoptante.id = ?1 and mascota.id = ?2", adoptanteId, mascotaId).firstResult();
    }
}
