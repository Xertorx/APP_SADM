package ucentral.edu.sadm.common.infraestructure;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    @Override
    public Response toResponse(BusinessException exception) {
        ResponseApi<Void> response = new ResponseApi<>(
                exception.getCode(),
                exception.getMessage()
        );

        return Response
                .status(exception.getCode())
                .entity(response)
                .build();
    }
}
