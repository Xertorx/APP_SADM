package ucentral.edu.sadm.usuarios.infraestructura;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import ucentral.edu.sadm.common.infraestructure.ResponseApi;
import ucentral.edu.sadm.usuarios.aplicacion.usuarioServicio;
import ucentral.edu.sadm.usuarios.infraestructura.dto.usuarioPublicoEntidad;
import ucentral.edu.sadm.usuarios.infraestructura.dto.usuarioRegistroEntidad;

import java.util.List;

@Path("/api/usuarios")
@Produces(MediaType.APPLICATION_JSON)
public class usuarioRecursos {

    @Inject
    usuarioServicio usuarioServicio;

    @POST
    @Path("/crear")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Crear un usuario personal")
    public Response crear(@Valid usuarioRegistroEntidad datos) {
        Long id = usuarioServicio.crear(datos);
        return Response.status(Response.Status.CREATED)
                .entity(new ResponseApi<>(201, "Usuario creado correctamente", id))
                .build();
    }

    @GET
    //@RolesAllowed("ADMINISTRADOR")
    @Operation(summary = "Consultar todos los usuarios")
    public List<usuarioPublicoEntidad> consultarTodos() {
        List<usuarioPublicoEntidad> datos = usuarioServicio.consultarTodos();
        return datos;
    }
}
