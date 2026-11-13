package ucentral.edu.sadm.administracion.infraestructura;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import ucentral.edu.sadm.administracion.aplicacion.estadisticasServicio;
import ucentral.edu.sadm.common.infraestructure.BusinessException;
import ucentral.edu.sadm.common.infraestructure.ResponseApi;

/** HU30: panel de estadísticas generales. Solo lectura y solo para ADMINISTRADOR. */
@Path("/api/admin/estadisticas")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMINISTRADOR")
public class estadisticasRecursos {

    @Inject
    estadisticasServicio servicio;

    @GET
    @Operation(summary = "Estadísticas generales (desde/hasta opcionales, formato yyyy-MM-dd)")
    public Response consultar(@QueryParam("desde") String desde, @QueryParam("hasta") String hasta) {
        return Response.ok(new ResponseApi<>(200, "Estadísticas consultadas", servicio.generar(desde, hasta))).build();
    }

    @GET
    @Path("/exportar")
    @Produces("text/csv")
    @Operation(summary = "Exportar el reporte consolidado en CSV (compatible con Excel en español)")
    public Response exportar(@QueryParam("desde") String desde, @QueryParam("hasta") String hasta) {
        byte[] csv;
        try {
            csv = servicio.exportarCsv(desde, hasta);
        } catch (BusinessException e) {
            // Este método produce text/csv: el error 400 se devuelve explícitamente como JSON
            // (el mapper global no fija el tipo y no habría serializador para ResponseApi en CSV).
            return Response.status(e.getCode())
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new ResponseApi<>(e.getCode(), e.getMessage()))
                    .build();
        }
        return Response.ok(csv, "text/csv; charset=UTF-8")
                .header("Content-Disposition", "attachment; filename=\"" + servicio.nombreArchivoCsv() + "\"")
                .build();
    }
}
