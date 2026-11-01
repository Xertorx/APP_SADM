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
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import ucentral.edu.sadm.common.infraestructure.ResponseApi;
import ucentral.edu.sadm.perfil.aplicacion.perfilServicio;
import ucentral.edu.sadm.perfil.dominio.fotoPerfil;
import ucentral.edu.sadm.perfil.infraestructura.dto.perfilEntidad;
import ucentral.edu.sadm.perfil.infraestructura.dto.perfilFormulario;

/** El usuario siempre sale del token (SecurityIdentity); nunca de un id en la ruta. */
@Path("api/perfil")
public class perfilRecursos {

    @Inject
    perfilServicio perfilServicio;

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

    private String correoAutenticado() {
        return securityIdentity.getPrincipal().getName();
    }
}
