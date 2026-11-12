package ucentral.edu.sadm.postulacionesMascotas.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class postulacionRepositorio implements PanacheRepository<postulacion> {

    public postulacion buscarPorAdoptanteYMascota(Long adoptanteId, Long mascotaId) {
        return find("adoptante.id = ?1 and mascota.id = ?2", adoptanteId, mascotaId).firstResult();
    }

    public boolean tieneActivasParaMascota(Long mascotaId) {
        return count("mascota.id = ?1 and estado = ?2", mascotaId, estadoPostulacion.ETAPA_1_EN_CURSO) > 0;
    }

    // ---- HU21: Mis postulaciones ----
    public java.util.List<postulacion> listarPorAdoptante(Long adoptanteId) {
        return find("select p from postulacion p join fetch p.mascota m join fetch m.dador "
                + "where p.adoptante.id = ?1 order by p.fecha_postulacion desc", adoptanteId).list();
    }
}
