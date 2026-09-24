package ucentral.edu.sadm.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import ucentral.edu.sadm.common.infraestructure.ResponseApi;

@Provider
public class JsonParseExceptionMapper implements ExceptionMapper<JsonProcessingException> {

    @Override
    public Response toResponse(JsonProcessingException exception) {

        ResponseApi<String> response =
                new ResponseApi<>(
                        400,
                        "El cuerpo de la petición está vacío o no es un JSON válido",
                        null
                );

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(response)
                .build();
    }
}

