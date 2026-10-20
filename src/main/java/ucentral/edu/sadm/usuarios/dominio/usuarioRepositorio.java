package ucentral.edu.sadm.usuarios.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Locale;

@ApplicationScoped
public class usuarioRepositorio implements PanacheRepository<usuario> {

    public usuario findByEmail(String email) {
        if (email == null) {
            return null;
        }
        return find("lower(correo)", email.trim().toLowerCase(Locale.ROOT)).firstResult();
    }
}
