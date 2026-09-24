package ucentral.edu.sadm.adoptantes.infraestructura;

import jakarta.inject.Inject;
import jakarta.validation.Valid;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import ucentral.edu.sadm.adoptantes.aplicaicon.adoptanteServicio;
import ucentral.edu.sadm.adoptantes.infraestructura.dto.adoptanteEntidad;
import ucentral.edu.sadm.common.infraestructure.ResponseApi;

import java.util.List;

@Path("/adoptantes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class adoptanteRecursos {
    @Inject
    adoptanteServicio adoptanteServicio;

    @POST
    @Path("/crear")
    @Operation(
            summary = "Crear un nuevo adoptante",
            description = "Registra los datos del adoptante"
    )
    public Response crear(@Valid adoptanteEntidad adoptanteEntidad){
        this.adoptanteServicio.crear(adoptanteEntidad);
        ResponseApi<List<adoptanteEntidad>> response =
                new ResponseApi<>(
                        201,
                        "Adoptante creado correctamente"
                );
        return Response.status(Response.Status.CREATED).entity(response).build();
    }


    @Operation(summary = "Lista todos los adoptantes")
    @GET
    @Path("/")
    public Response consultarTodos(){
        List<adoptanteEntidad> datos = this.adoptanteServicio.consultarTodos();
        return Response.status(Response.Status.OK).entity(datos).build();
    }

}
