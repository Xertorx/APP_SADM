package ucentral.edu.sadm.fundaciones.infraestructura;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import ucentral.edu.sadm.common.infraestructure.ResponseApi;
import ucentral.edu.sadm.fundaciones.aplicacion.fundacionServicio;
import ucentral.edu.sadm.fundaciones.infraestructura.dto.fundacionRegistroEntidad;
import ucentral.edu.sadm.fundaciones.infraestructura.dto.fundacionPublicaEntidad;

import java.util.List;

@Path("/api/fundaciones")
@Produces(MediaType.APPLICATION_JSON)
public class fundacionRecursos {

    @Inject
    fundacionServicio fundacionServicio;

    @POST
    @Path("/crear")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Registrar una fundación")
    public Response crear(@Valid fundacionRegistroEntidad datos) {
        Long id = fundacionServicio.crear(datos);
        return Response.status(Response.Status.CREATED)
                .entity(new ResponseApi<>(201, "Fundación registrada y pendiente de revisión", id))
                .build();
    }

    @PATCH
    @Path("/{id}/aprobar")
    @RolesAllowed("ADMINISTRADOR")
    @Operation(summary = "Aprobar una fundación")
    public Response aprobar(@PathParam("id") Long id) {
        fundacionServicio.aprobar(id);
        return Response.ok(new ResponseApi<>(200, "Fundación aprobada")).build();
    }

    @GET
    @RolesAllowed("ADMINISTRADOR")
    @Operation(summary = "Consultar fundaciones registradas")
    public List<fundacionPublicaEntidad> consultarTodas() {
        List<fundacionPublicaEntidad> datos = fundacionServicio.consultarTodas();
        return datos;
    }
}
