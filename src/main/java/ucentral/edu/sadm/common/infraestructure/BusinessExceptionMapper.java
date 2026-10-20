package ucentral.edu.sadm.common.infraestructure;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    @Override
    public Response toResponse(BusinessException exception) {
        ResponseApi<?> response;
        if (exception.getErrorCode() == null) {
            response = new ResponseApi<>(
                    exception.getCode(),
                    exception.getMessage()
            );
        } else {
            response = new ResponseApi<>(
                    exception.getCode(),
                    exception.getMessage(),
                    new ErrorDetail(exception.getErrorCode(), exception.getDetail())
            );
        }

        return Response
                .status(exception.getCode())
                .entity(response)
                .build();
    }

    public record ErrorDetail(String codigo, String detalle) {
    }
}
