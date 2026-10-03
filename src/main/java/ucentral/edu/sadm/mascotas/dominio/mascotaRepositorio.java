package ucentral.edu.sadm.mascotas.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class mascotaRepositorio implements PanacheRepository<mascota> {

}
