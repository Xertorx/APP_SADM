package ucentral.edu.sadm.autenticarse.infraestructura;

import io.quarkus.arc.profile.IfBuildProfile;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import ucentral.edu.sadm.common.infraestructure.ResponseApi;
import ucentral.edu.sadm.usuarios.aplicacion.usuarioServicio;
import ucentral.edu.sadm.usuarios.infraestructura.dto.restablecerContrasenaLocalSolicitud;

@Path("/api/desarrollo")
@IfBuildProfile("dev")
@Produces(MediaType.APPLICATION_JSON)
public class restablecerContrasenaLocalRecursos {

    @Inject
    usuarioServicio usuarioServicio;

    @POST
    @Path("/restablecer-contrasena")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Restablecer una contraseña en el perfil local de desarrollo")
    public Response restablecer(@Valid restablecerContrasenaLocalSolicitud solicitud) {
        usuarioServicio.restablecerContrasenaLocal(solicitud.correo(), solicitud.contraseñaNueva());
        return Response.ok(new ResponseApi<>(200, "Contraseña actualizada")).build();
    }
}
