package ucentral.edu.sadm.mascotas.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class mascotaRepositorio implements PanacheRepository<mascota> {

    public List<mascota> listarTodas() {
        return listAll();
    }
}
