package ucentral.edu.sadm.perfil.infraestructura;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import ucentral.edu.sadm.common.infraestructure.ResponseApi;
import ucentral.edu.sadm.perfil.aplicacion.perfilServicio;
import ucentral.edu.sadm.perfil.aplicacion.ubicacionPerfilServicio;
import ucentral.edu.sadm.perfil.infraestructura.dto.ubicacionPerfilEntidad;
import ucentral.edu.sadm.perfil.dominio.fotoPerfil;
import ucentral.edu.sadm.perfil.infraestructura.dto.perfilEntidad;
import ucentral.edu.sadm.perfil.infraestructura.dto.perfilFormulario;
import ucentral.edu.sadm.perfil.infraestructura.dto.perfilPublicoEntidad;

/** El usuario siempre sale del token (SecurityIdentity); nunca de un id en la ruta. */
@Path("api/perfil")
public class perfilRecursos {

    @Inject
    perfilServicio perfilServicio;

    @Inject
    ubicacionPerfilServicio ubicacionPerfilServicio;

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USUARIO", "FUNDACION", "ADMINISTRADOR"})
    @Operation(summary = "Consultar mi perfil")
    public Response consultar() {
        perfilEntidad perfil = perfilServicio.consultar(correoAutenticado());
        return Response.ok(new ResponseApi<>(200, "Perfil consultado", perfil)).build();
    }

    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USUARIO", "FUNDACION", "ADMINISTRADOR"})
    @Operation(
            summary = "Editar mi perfil",
            description = "Actualiza datos de contacto, foto (opcional) y contraseña (opcional en bloque) en una sola transacción"
    )
    public Response actualizar(@Valid @BeanParam perfilFormulario formulario) {
        perfilServicio.actualizar(formulario.datos, formulario.foto, correoAutenticado());
        return Response.ok(new ResponseApi<>(200, "Tus cambios se guardaron correctamente")).build();
    }

    @GET
    @Path("/foto")
    @RolesAllowed({"USUARIO", "FUNDACION", "ADMINISTRADOR"})
    @Operation(summary = "Descargar mi foto de perfil")
    public Response consultarFoto() {
        fotoPerfil foto = perfilServicio.consultarFoto(correoAutenticado());
        CacheControl sinCache = new CacheControl();
        sinCache.setNoCache(true);
        return Response.ok(foto.datos, foto.tipo_contenido).cacheControl(sinCache).build();
    }

    @GET
    @Path("/publico/{usuarioId}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USUARIO", "FUNDACION", "ADMINISTRADOR"})
    @Operation(
            summary = "Consultar el perfil público de un usuario (dador)",
            description = "Solo nombre, apellido, si tiene foto y desde cuándo es miembro; nunca datos de contacto"
    )
    public Response consultarPublico(@PathParam("usuarioId") Long usuarioId) {
        perfilPublicoEntidad perfil = perfilServicio.consultarPublico(usuarioId);
        return Response.ok(new ResponseApi<>(200, "Perfil público consultado", perfil)).build();
    }

    @GET
    @Path("/publico/{usuarioId}/foto")
    @RolesAllowed({"USUARIO", "FUNDACION", "ADMINISTRADOR"})
    @Operation(summary = "Descargar la foto de perfil pública de un usuario (dador)")
    public Response consultarFotoPublica(@PathParam("usuarioId") Long usuarioId) {
        fotoPerfil foto = perfilServicio.consultarFotoPublica(usuarioId);
        CacheControl sinCache = new CacheControl();
        sinCache.setNoCache(true);
        return Response.ok(foto.datos, foto.tipo_contenido).cacheControl(sinCache).build();
    }

    // ---- HU10: ubicación de referencia (respaldo cuando el usuario niega el GPS) ----

    @GET
    @Path("/ubicacion")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USUARIO", "FUNDACION", "ADMINISTRADOR"})
    @Operation(summary = "Consultar mi ubicación de referencia", description = "404 si aún no la registraste")
    public Response consultarUbicacion() {
        return Response.ok(new ResponseApi<>(200, "Ubicación consultada",
                ubicacionPerfilServicio.consultar(correoAutenticado()))).build();
    }

    @PUT
    @Path("/ubicacion")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USUARIO", "FUNDACION", "ADMINISTRADOR"})
    @Operation(
            summary = "Guardar mi ubicación de referencia",
            description = "Latitud -90..90, longitud -180..180, localidad opcional (código del catálogo). Se guarda a 3 decimales"
    )
    public Response guardarUbicacion(@Valid ubicacionPerfilEntidad datos) {
        return Response.ok(new ResponseApi<>(200, "Ubicación guardada correctamente",
                ubicacionPerfilServicio.guardar(datos, correoAutenticado()))).build();
    }

    private String correoAutenticado() {
        return securityIdentity.getPrincipal().getName();
    }
}
