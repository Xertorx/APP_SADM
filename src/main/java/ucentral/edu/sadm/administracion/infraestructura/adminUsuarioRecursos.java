package ucentral.edu.sadm.administracion.infraestructura;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import ucentral.edu.sadm.administracion.aplicacion.adminUsuarioServicio;
import ucentral.edu.sadm.administracion.infraestructura.dto.inhabilitarSolicitud;
import ucentral.edu.sadm.administracion.infraestructura.dto.usuarioAdminDetalleEntidad;
import ucentral.edu.sadm.administracion.infraestructura.dto.usuarioAdminEntidad;
import ucentral.edu.sadm.common.infraestructure.ResponseApi;

import java.util.List;

/** HU27 + HU28. Todos los endpoints son solo para ADMINISTRADOR; el admin sale del token. */
@Path("/api/admin/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMINISTRADOR")
public class adminUsuarioRecursos {

    @Inject
    adminUsuarioServicio servicio;

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Operation(summary = "Buscar usuarios por nombre (parcial, sin tildes ni mayúsculas)")
    public Response buscar(@QueryParam("nombre") String nombre) {
        List<usuarioAdminEntidad> datos = servicio.buscar(nombre);
        return Response.ok(new ResponseApi<>(200, "Usuarios consultados", datos)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Detalle de un usuario, su actividad y su historial de moderación")
    public Response detalle(@PathParam("id") Long id) {
        return Response.ok(new ResponseApi<>(200, "Usuario consultado", servicio.detalle(id))).build();
    }

    @POST
    @Path("/{id}/inhabilitar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Inhabilitar una cuenta (motivo obligatorio, 10–300 caracteres)")
    public Response inhabilitar(@PathParam("id") Long id, inhabilitarSolicitud solicitud) {
        String motivo = solicitud == null ? null : solicitud.motivo();
        usuarioAdminDetalleEntidad datos = servicio.inhabilitar(id, motivo, correoAutenticado());
        return Response.ok(new ResponseApi<>(200, "Cuenta inhabilitada correctamente", datos)).build();
    }

    @POST
    @Path("/{id}/reactivar")
    @Operation(summary = "Reactivar una cuenta inhabilitada")
    public Response reactivar(@PathParam("id") Long id) {
        usuarioAdminDetalleEntidad datos = servicio.reactivar(id, correoAutenticado());
        return Response.ok(new ResponseApi<>(200, "Cuenta reactivada correctamente", datos)).build();
    }

    private String correoAutenticado() {
        return securityIdentity.getPrincipal().getName();
    }
}
