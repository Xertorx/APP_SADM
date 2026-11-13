package ucentral.edu.sadm.perfil.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ubicacionUsuarioRepositorio implements PanacheRepository<ubicacionUsuario> {

    public ubicacionUsuario findByUsuarioId(Long usuarioId) {
        return find("usuario.id", usuarioId).firstResult();
    }
}
