package ucentral.edu.sadm.perfil.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class fotoPerfilRepositorio implements PanacheRepository<fotoPerfil> {

    public fotoPerfil findByUsuarioId(Long usuarioId) {
        return find("usuario.id", usuarioId).firstResult();
    }
}
