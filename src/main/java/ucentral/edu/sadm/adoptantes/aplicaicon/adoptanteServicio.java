package ucentral.edu.sadm.adoptantes.aplicaicon;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import ucentral.edu.sadm.adoptantes.dominio.adoptante;
import ucentral.edu.sadm.adoptantes.dominio.adoptanteRepositorio;
import ucentral.edu.sadm.adoptantes.dominio.exception.EmailAlreadyExistsException;
import ucentral.edu.sadm.adoptantes.infraestructura.dto.adoptanteEntidad;
import ucentral.edu.sadm.common.service.PasswordService;

import java.util.List;

@ApplicationScoped
public class adoptanteServicio {
    private static final Logger LOG = Logger.getLogger(adoptanteServicio.class);

    @Inject
    adoptanteRepositorio adoptanteRepositorio;
    @Inject
    PasswordService passwordService;

    @Transactional
    public void crear(adoptanteEntidad adoptanteEntidad){

        if(adoptanteRepositorio.findByEmail(adoptanteEntidad.correo()) != null){

            throw new EmailAlreadyExistsException();
        }

        adoptante adoptante = new adoptante(
                adoptanteEntidad.nombre(),
                adoptanteEntidad.apellido(),
                adoptanteEntidad.identificacion(),
                adoptanteEntidad.fecha_nacimiento(),
                adoptanteEntidad.correo(),
                passwordService.hash(adoptanteEntidad.contraseña()),
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
