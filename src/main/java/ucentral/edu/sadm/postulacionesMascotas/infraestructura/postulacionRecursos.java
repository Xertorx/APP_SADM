package ucentral.edu.sadm.postulacionesMascotas.infraestructura;

import jakarta.inject.Inject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.groups.ConvertGroup;
import jakarta.validation.groups.Default;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import io.quarkus.security.identity.SecurityIdentity;
import ucentral.edu.sadm.usuarios.dominio.usuario;
import ucentral.edu.sadm.usuarios.dominio.usuarioRepositorio;
import ucentral.edu.sadm.common.infraestructure.BusinessException;
import ucentral.edu.sadm.common.infraestructure.ResponseApi;
import ucentral.edu.sadm.postulacionesMascotas.aplicaicon.postulacionServicio;
import ucentral.edu.sadm.postulacionesMascotas.infraestructura.dto.postulacionEntidad;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("USUARIO")
public class postulacionRecursos {

    @Inject
    postulacionServicio postulacionServicio;

    @Inject
    usuarioRepositorio usuarioRepositorio;

    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Path("/postulaciones/mascotas/{mascotaId}/resumen")
    @Operation(
            summary = "Consultar el resumen previo a postularse",
            description = "Muestra los datos de la mascota y las etapas del proceso sin crear una postulación"
    )
    @APIResponse(
            responseCode = "200",
            description = "Resumen del proceso",
            content = @Content(examples = {
                    @ExampleObject(
                            name = "Postulación activa",
                            value = """
                                    {"status":200,"message":"Consulta exitosa","data":{"mascota":{"id":12,"nombre":"Luna","foto":"/SADM/mascotas/fotos/4","especie":"Perro","raza":"Criollo"},"etapas":[{"nombre":"Revisión inicial","descripcion":"Validación de los datos y requisitos del adoptante."}],"puedePostularse":false,"motivo":"POSTULACION_DUPLICADA","postulacionActiva":true,"estado":"ETAPA_1_EN_CURSO","mensaje":"Ya tienes una postulación activa para esta mascota"}}
                                    """
                    ),
                    @ExampleObject(
                            name = "Puede postularse",
                            value = """
                                    {"status":200,"message":"Consulta exitosa","data":{"mascota":{"id":12,"nombre":"Luna","foto":"/SADM/mascotas/fotos/4","especie":"Perro","raza":"Criollo"},"etapas":[{"nombre":"Revisión inicial","descripcion":"Validación de los datos y requisitos del adoptante."}],"puedePostularse":true,"postulacionActiva":false,"mensaje":"Puedes postularte a esta mascota"}}
                                    """
                    )
            })
    )
    @APIResponse(
            responseCode = "404",
            description = "La mascota no existe",
            content = @Content(examples = @ExampleObject(value = """
                    {"status":404,"message":"La mascota no existe","data":{"codigo":"MASCOTA_NO_ENCONTRADA","detalle":"No se encontró una mascota con el identificador solicitado"}}
                    """))
    )
    @APIResponse(responseCode = "401", description = "Se requiere autenticación")
    @APIResponse(responseCode = "403", description = "Se requiere el rol USUARIO")
    public Response resumen(@PathParam("mascotaId") Long mascotaId) {
        postulacionEntidad datos = postulacionServicio.resumen(mascotaId, obtenerAdoptanteId());
        return Response.ok(new ResponseApi<>(200, "Consulta exitosa", datos)).build();
    }

    @POST
    @Path("/postulaciones")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Crear una postulación a una mascota",
            description = "Registra la postulación del adoptante autenticado; la fecha y el estado se asignan en el servidor"
    )
    @APIResponse(
            responseCode = "201",
            description = "Postulación creada",
            content = @Content(examples = @ExampleObject(value = """
                    {"status":201,"message":"Postulación creada correctamente","data":{"id":34,"mascotaId":12,"adoptanteId":8,"fecha":"2026-10-14T23:30:00Z","estado":"ETAPA_1_EN_CURSO","puntaje":0,"postulacionActiva":true,"mensaje":"Postulación creada correctamente"}}
                    """))
    )
    @APIResponse(
            responseCode = "409",
            description = "Mascota no disponible o postulación duplicada",
            content = @Content(examples = @ExampleObject(value = """
                    {"status":409,"message":"Ya existe una postulación de este adoptante para la mascota","data":{"codigo":"POSTULACION_DUPLICADA","detalle":"Un adoptante solo puede postularse una vez a cada mascota"}}
                    """))
    )
    @APIResponse(
            responseCode = "404",
            description = "La mascota no existe",
            content = @Content(examples = @ExampleObject(value = """
                    {"status":404,"message":"La mascota no existe","data":{"codigo":"MASCOTA_NO_ENCONTRADA","detalle":"No se encontró una mascota con el identificador solicitado"}}
                    """))
    )
    @APIResponse(responseCode = "400", description = "El cuerpo no contiene un mascotaId positivo y numérico")
    @APIResponse(responseCode = "401", description = "Se requiere autenticación")
    @APIResponse(responseCode = "403", description = "Se requiere el rol USUARIO")
    public Response crear(
            @NotNull
            @Valid
            @ConvertGroup(from = Default.class, to = postulacionEntidad.Solicitud.class)
            postulacionEntidad solicitud
    ) {
        postulacionEntidad datos = postulacionServicio.crear(obtenerAdoptanteId(), solicitud.mascotaId());
        return Response.status(Response.Status.CREATED)
                .entity(new ResponseApi<>(201, "Postulación creada correctamente", datos))
                .build();
    }

    private Long obtenerAdoptanteId() {
        usuario adoptante = usuarioRepositorio.findByEmail(securityIdentity.getPrincipal().getName());
        if (adoptante == null) {
            throw new BusinessException(
                    403,
                    "ADOPTANTE_NO_ENCONTRADO",
                    "El usuario autenticado no está registrado como adoptante",
                    "La identidad autenticada debe corresponder al correo de un adoptante registrado"
            );
        }
        return adoptante.id;
    }
}
