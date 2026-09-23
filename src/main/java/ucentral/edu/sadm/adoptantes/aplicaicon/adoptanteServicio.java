package ucentral.edu.sadm.adoptantes.aplicaicon;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import ucentral.edu.sadm.adoptantes.dominio.adoptante;
import ucentral.edu.sadm.adoptantes.dominio.adoptanteRepositorio;
import ucentral.edu.sadm.adoptantes.infraestructura.dto.adoptanteEntidad;

import java.util.List;

@ApplicationScoped
public class adoptanteServicio {
    private static final Logger LOG = Logger.getLogger(adoptanteServicio.class);

    @Inject
    adoptanteRepositorio adoptanteRepositorio;
    @Transactional
    public void crear(adoptanteEntidad adoptanteEntidad){
        // Se mapea el Dto que llega por el Entity
        adoptante adoptante = new adoptante(
                adoptanteEntidad.nombre(),
                adoptanteEntidad.apellido(),
                adoptanteEntidad.identificacion(),
                adoptanteEntidad.fecha_nacimiento(),
                adoptanteEntidad.correo(),
                adoptanteEntidad.contraseña(),
                adoptanteEntidad.telefono(),
                adoptanteEntidad.direccion(),
                adoptanteEntidad.tipo_usuario(),
                adoptanteEntidad.estado()
        );

        adoptanteRepositorio.persist(adoptante);
    }

    public List<adoptanteEntidad> consultarTodos() {
        List<adoptante> adoptantes = adoptanteRepositorio.listAll();

        return adoptantes.stream()
                .map(p -> new adoptanteEntidad(p.nombre, p.apellido, p.identificacion, p.fecha_nacimiento, p.correo, p.contraseña, p.telefono, p.direccion, p.tipo_usuario, p.estado))
                .toList();


    }
}
