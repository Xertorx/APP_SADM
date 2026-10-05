package ucentral.edu.sadm.mascotas.dominio;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class fotoMascotaRepositorio implements PanacheRepository<fotoMascota> {

    public List<fotoMascota> listarPorMascota(Long mascotaId) {
        return list("mascota.id", mascotaId);
    }

}
