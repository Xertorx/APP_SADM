package ucentral.edu.sadm.perfil.infraestructura.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

public class perfilFormulario {

    @RestForm("datos")
    @PartType(MediaType.APPLICATION_JSON)
    @NotNull(message = "Los datos del perfil son obligatorios")
    @Valid
    public perfilActualizacionEntidad datos;

    @RestForm("foto")
    public FileUpload foto;
}
