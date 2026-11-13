package ucentral.edu.sadm.ubicaciones.aplicacion;

import jakarta.enterprise.context.ApplicationScoped;
import ucentral.edu.sadm.ubicaciones.dominio.localidad;
import ucentral.edu.sadm.ubicaciones.dominio.localidadesBogota;

import java.util.List;

@ApplicationScoped
public class ubicacionServicio {

    public List<localidad> listarLocalidades() {
        return localidadesBogota.TODAS;
    }
}
