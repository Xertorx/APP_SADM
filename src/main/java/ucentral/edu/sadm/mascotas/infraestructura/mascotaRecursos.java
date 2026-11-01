package ucentral.edu.sadm.mascotas.infraestructura;

import jakarta.inject.Inject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import io.quarkus.security.identity.SecurityIdentity;
import ucentral.edu.sadm.common.infraestructure.ResponseApi;
import ucentral.edu.sadm.mascotas.aplicaicon.mascotaServicio;
import ucentral.edu.sadm.mascotas.dominio.fotoMascota;
import ucentral.edu.sadm.mascotas.infraestructura.dto.mascotaCreadaEntidad;
import ucentral.edu.sadm.mascotas.infraestructura.dto.mascotaDetalleEntidad;
import ucentral.edu.sadm.mascotas.infraestructura.dto.mascotaEdicionEntidad;
import ucentral.edu.sadm.mascotas.infraestructura.dto.mascotaEliminacionEntidad;
import ucentral.edu.sadm.mascotas.infraestructura.dto.mascotaFormulario;

import java.util.List;

@Path("api/mascotas")
public class mascotaRecursos {
    @Inject
    mascotaServicio mascotaServicio;

    @Inject
    SecurityIdentity securityIdentity;

    @POST
    @Path("/crear")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Publicar una mascota",
            description = "Registra los datos y las fotos de una mascota; queda pendiente de aprobación"
    )
    @RolesAllowed({"USUARIO", "FUNDACION"})
    public Response crear(@Valid @BeanParam mascotaFormulario formulario) {
        Long id = this.mascotaServicio.crear(
                formulario.datos,
                formulario.fotos,
                securityIdentity.getPrincipal().getName()
        );
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
    public mascotaDetalleEntidad consultar(@PathParam("id") Long id) {
        mascotaDetalleEntidad datos = this.mascotaServicio.consultar(id);
        return datos;
    }

    @GET
    @Path("/{id}/editar")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USUARIO", "FUNDACION"})
    @Operation(summary = "Consultar los datos de una mascota para editar")
    public Response consultarParaEditar(@PathParam("id") Long id) {
        mascotaEdicionEntidad datos = mascotaServicio.consultarParaEditar(
                id,
                securityIdentity.getPrincipal().getName()
        );
        return Response.ok(new ResponseApi<>(200, "Formulario de edición", datos)).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USUARIO", "FUNDACION"})
    @Operation(
            summary = "Editar una publicación de mascota",
            description = "Actualiza los datos validados; conserva las fotos existentes si no se envían fotos nuevas"
    )
    public Response actualizar(
            @PathParam("id") Long id,
            @Valid @BeanParam mascotaFormulario formulario
    ) {
        mascotaServicio.actualizar(
                id,
                formulario.datos,
                formulario.fotos,
                securityIdentity.getPrincipal().getName()
        );
        return Response.ok(new ResponseApi<>(200, "La publicación fue actualizada correctamente")).build();
    }

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Consultar todas las mascotas publicadas")
    public List<mascotaDetalleEntidad> consultar() {
        List<mascotaDetalleEntidad> datos = this.mascotaServicio.consultarTodas();
        return datos;
    }

    @GET
    @Path("/mis-publicaciones")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USUARIO", "FUNDACION"})
    @Operation(summary = "Consultar las mascotas del dador autenticado")
    public List<mascotaDetalleEntidad> consultarMisPublicaciones() {
        return mascotaServicio.consultarDelDador(securityIdentity.getPrincipal().getName());
    }

    @GET
    @Path("/fotos/{idFoto}")
    @Operation(summary = "Obtener los bytes de una foto de mascota")
    public fotoMascota consultarFoto(@PathParam("idFoto") Long idFoto) {
        fotoMascota foto = this.mascotaServicio.consultarFoto(idFoto);
        return foto;
    }

    @GET
    @Path("/{id}/eliminacion")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USUARIO", "FUNDACION"})
    @Operation(summary = "Consultar los datos para confirmar la eliminación de una publicación")
    public Response consultarParaEliminar(@PathParam("id") Long id) {
        mascotaEliminacionEntidad datos = mascotaServicio.consultarParaEliminar(
                id,
                securityIdentity.getPrincipal().getName()
        );
        return Response.ok(new ResponseApi<>(200, "Datos para eliminar", datos)).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USUARIO", "FUNDACION"})
    @Operation(
            summary = "Eliminar una publicación de mascota",
            description = "Borrado lógico; cancela las postulaciones activas y notifica a los candidatos"
    )
    public Response eliminar(@PathParam("id") Long id) {
        mascotaServicio.eliminar(id, securityIdentity.getPrincipal().getName());
        return Response.ok(new ResponseApi<>(200, "La publicación fue eliminada")).build();
    }
}
