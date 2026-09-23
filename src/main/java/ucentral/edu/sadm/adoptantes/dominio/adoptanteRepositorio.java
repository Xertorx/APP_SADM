package ucentral.edu.sadm.adoptantes.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class adoptanteRepositorio implements PanacheRepository<adoptante> {
}
