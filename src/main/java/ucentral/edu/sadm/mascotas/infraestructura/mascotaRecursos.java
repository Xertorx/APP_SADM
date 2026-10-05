package ucentral.edu.sadm.mascotas.infraestructura;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.jboss.resteasy.reactive.MultipartForm;
import ucentral.edu.sadm.common.infraestructure.ResponseApi;
import ucentral.edu.sadm.mascotas.aplicaicon.mascotaServicio;
import ucentral.edu.sadm.mascotas.dominio.fotoMascota;
import ucentral.edu.sadm.mascotas.infraestructura.dto.mascotaCreadaEntidad;
import ucentral.edu.sadm.mascotas.infraestructura.dto.mascotaDetalleEntidad;
import ucentral.edu.sadm.mascotas.infraestructura.dto.mascotaFormulario;

@Path("/mascotas")
public class mascotaRecursos {
    @Inject
    mascotaServicio mascotaServicio;

    @POST
    @Path("/crear")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Publicar una mascota",
            description = "Registra los datos y las fotos de una mascota; queda pendiente de aprobación"
    )
    public Response crear(@Valid @MultipartForm mascotaFormulario formulario) {
        Long id = this.mascotaServicio.crear(formulario.datos, formulario.fotos);
        ResponseApi<mascotaCreadaEntidad> response = new ResponseApi<>(
                201,
                "Tu publicación fue enviada y está en revisión por el administrador",
                new mascotaCreadaEntidad(id)
        );
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Consultar una mascota publicada")
    public Response consultar(@PathParam("id") Long id) {
        mascotaDetalleEntidad datos = this.mascotaServicio.consultar(id);
        ResponseApi<mascotaDetalleEntidad> response = new ResponseApi<>(200, "Consulta exitosa", datos);
        return Response.status(Response.Status.OK).entity(response).build();
    }

    @GET
    @Path("/fotos/{idFoto}")
    @Operation(summary = "Obtener los bytes de una foto de mascota")
    public Response consultarFoto(@PathParam("idFoto") Long idFoto) {
        fotoMascota foto = this.mascotaServicio.consultarFoto(idFoto);
        return Response.ok(foto.datos).type(foto.tipo_contenido).build();
    }
}
