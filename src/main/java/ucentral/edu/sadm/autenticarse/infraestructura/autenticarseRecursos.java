package ucentral.edu.sadm.autenticarse.infraestructura;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import ucentral.edu.sadm.autenticarse.aplicacion.autenticarseServicio;
import ucentral.edu.sadm.autenticarse.infraestructura.dto.loginRespuesta;
import ucentral.edu.sadm.autenticarse.infraestructura.dto.loginSolicitud;
import ucentral.edu.sadm.common.infraestructure.ResponseApi;

@Path("/api/autenticacion")
@Produces(MediaType.APPLICATION_JSON)
public class autenticarseRecursos {

    @Inject
    autenticarseServicio autenticarseServicio;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Iniciar sesión y obtener un JWT")
    public Response iniciarSesion(@Valid loginSolicitud solicitud) {
        loginRespuesta datos = autenticarseServicio.iniciarSesion(solicitud);
        return Response.ok(new ResponseApi<>(200, "Inicio de sesión exitoso", datos)).build();
    }

    @POST
    @Path("/sesion")
    @RolesAllowed({"USUARIO", "FUNDACION", "ADMINISTRADOR"})
    @Operation(summary = "Validar la sesión autenticada")
    public Response sesion() {
        return Response.ok(new ResponseApi<>(200, "Sesión activa")).build();
    }
}
