package ucentral.edu.sadm.common;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import ucentral.edu.sadm.common.infraestructure.ResponseApi;

import java.util.HashMap;
import java.util.Map;


@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {

        Map<String, String> errores = new HashMap<>();

        exception.getConstraintViolations()
                .forEach(error -> {
                    String path = error.getPropertyPath().toString();
                    String campo = path.contains(".")
                            ? path.substring(path.lastIndexOf('.') + 1)
                            : path;
                    errores.put(campo, error.getMessage());
                });

        ResponseApi<Map<String, String>> response =
                new ResponseApi<>(
                        400,
                        "Error de validación",
                        errores
                );

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(response)
                .build();
    }

}


