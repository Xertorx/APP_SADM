package ucentral.edu.sadm.seleccion.infraestructura;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import ucentral.edu.sadm.common.infraestructure.ResponseApi;
import ucentral.edu.sadm.seleccion.aplicacion.configuracionSeleccionServicio;
import ucentral.edu.sadm.seleccion.infraestructura.dto.configuracionSeleccionEntidad;
import ucentral.edu.sadm.seleccion.infraestructura.dto.configuracionSeleccionSolicitud;

@Path("/api/mascotas/{mascotaId}/configuracion-seleccion")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"USUARIO", "FUNDACION"})
public class configuracionSeleccionRecursos {

    @Inject
    configuracionSeleccionServicio configuracionServicio;

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Operation(
            summary = "Consultar la configuración de selección de una mascota",
            description = "Devuelve un borrador vacío si aún no se ha guardado configuración"
    )
    public Response consultar(@PathParam("mascotaId") Long mascotaId) {
        configuracionSeleccionEntidad datos = configuracionServicio.consultar(mascotaId, correoDador());
        return Response.ok(new ResponseApi<>(200, "Configuración consultada correctamente", datos)).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Crear o actualizar el borrador de selección",
            description = "Reemplaza la configuración completa. Los elementos con ID se actualizan; los omitidos se eliminan. La distribución y las cuatro etapas se validan al finalizar."
    )
    public Response guardar(
            @PathParam("mascotaId") Long mascotaId,
            @NotNull @Valid configuracionSeleccionSolicitud solicitud
    ) {
        configuracionSeleccionEntidad datos = configuracionServicio.guardar(mascotaId, correoDador(), solicitud);
        return Response.ok(new ResponseApi<>(200, "Borrador de configuración guardado correctamente", datos)).build();
    }

    @POST
    @Path("/finalizar")
    @Operation(
            summary = "Finalizar la configuración de selección",
            description = "Exige al menos un elemento en cada etapa, puntajes dentro de sus máximos y una distribución total exacta de 100 puntos"
    )
    public Response finalizar(@PathParam("mascotaId") Long mascotaId) {
        configuracionSeleccionEntidad datos = configuracionServicio.finalizar(mascotaId, correoDador());
        return Response.ok(new ResponseApi<>(200, "Configuración finalizada correctamente", datos)).build();
    }

    private String correoDador() {
        return securityIdentity.getPrincipal().getName();
    }
}
