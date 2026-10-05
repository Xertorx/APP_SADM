package ucentral.edu.sadm.mascotas.infraestructura.dto;

import jakarta.validation.Valid;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.List;

public class mascotaFormulario {

    @RestForm("datos")
    @PartType(MediaType.APPLICATION_JSON)
    @Valid
    public mascotaEntidad datos;

    @RestForm("fotos")
    public List<FileUpload> fotos;
}
