package ucentral.edu.sadm.ubicaciones.infraestructura;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import ucentral.edu.sadm.ubicaciones.aplicacion.ubicacionServicio;
import ucentral.edu.sadm.ubicaciones.dominio.localidad;

import java.util.List;

/** HU10: catálogo de localidades de Bogotá (solo lectura). */
@Path("api/ubicaciones")
public class ubicacionRecursos {

    @Inject
    ubicacionServicio ubicacionServicio;

    @GET
    @Path("/localidades")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USUARIO", "FUNDACION", "ADMINISTRADOR"})
    @Operation(
            summary = "Listar las localidades de Bogotá",
            description = "20 localidades con las coordenadas aproximadas de su centro [{codigo, nombre, latitud, longitud}]"
    )
    public List<localidad> localidades() {
        return ubicacionServicio.listarLocalidades();
    }
}
