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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import io.quarkus.security.identity.SecurityIdentity;
import ucentral.edu.sadm.common.infraestructure.ResponseApi;
import ucentral.edu.sadm.mascotas.aplicaicon.mascotaBusquedaServicio;
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
    mascotaBusquedaServicio mascotaBusquedaServicio;

    @Inject
    SecurityIdentity securityIdentity;

    @POST
    @Path("/crear")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Crear un borrador de mascota",
            description = "Registra los datos y las fotos de una mascota; debe configurarse el proceso de selección antes de enviarla a revisión"
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
                "La mascota fue guardada como borrador. Configura las etapas de selección y envíala a revisión.",
                new mascotaCreadaEntidad(id)
        );
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST
    @Path("/{id}/publicar")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USUARIO", "FUNDACION"})
    @Operation(
            summary = "Enviar una mascota a revisión",
            description = "Solo permite enviar a revisión una mascota con las cuatro etapas de selección completas y 100 puntos distribuidos"
    )
    public Response publicar(@PathParam("id") Long id) {
        mascotaServicio.publicar(id, securityIdentity.getPrincipal().getName());
        return Response.ok(new ResponseApi<>(200, "La mascota fue enviada a revisión")).build();
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

    @GET
    @Path("/fotos/{idFoto}/imagen")
    @Operation(
            summary = "Obtener la imagen de una foto de mascota",
            description = "Devuelve los bytes con su Content-Type, para usarla directo en un <img> o en el navegador"
    )
    public Response consultarImagen(@PathParam("idFoto") Long idFoto) {
        fotoMascota foto = this.mascotaServicio.consultarFoto(idFoto);
        return Response.ok(foto.datos, foto.tipo_contenido).build();
    }

    // ---- HU08-HU09: búsqueda por nombre y filtros ----

    @GET
    @Path("/buscar")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Buscar mascotas disponibles por nombre y filtros",
            description = "Solo mascotas DISPONIBLES. Sin distinguir mayúsculas ni tildes. Filtros distintos con lógica Y; "
                    + "valores repetidos de especie/edad/tamano con lógica O; temperamento exige todos los marcados"
    )
    public List<mascotaDetalleEntidad> buscar(
            @QueryParam("nombre") String nombre,
            @QueryParam("especie") List<String> especie,
            @QueryParam("raza") String raza,
            @QueryParam("edad") List<String> edad,
            @QueryParam("tamano") List<String> tamano,
            @QueryParam("temperamento") List<String> temperamento
    ) {
        return mascotaBusquedaServicio.buscar(nombre, especie, raza, edad, tamano, temperamento);
    }

    @GET
    @Path("/razas")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Listar las razas de las mascotas disponibles de una especie")
    public List<String> razas(@QueryParam("especie") String especie) {
        return mascotaBusquedaServicio.razasPorEspecie(especie);
    }
}
