package ucentral.edu.sadm.fundaciones.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class fundacionRepositorio implements PanacheRepository<fundacion> {

    public fundacion findByNit(String nit) {
        return find("nit", nit.trim()).firstResult();
    }

    public fundacion findByUsuarioId(Long usuarioId) {
        return find("usuario.id", usuarioId).firstResult();
    }
}
