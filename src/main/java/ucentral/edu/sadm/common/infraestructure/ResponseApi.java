package ucentral.edu.sadm.common.infraestructure;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseApi<T> {
    public int status;
    public String message;
    public T data;

    public ResponseApi( ) {
    }
    public ResponseApi(int status ) {
        this.status = status;
    }
    public ResponseApi(int status, String message ) {
        this.status = status;
        this.message = message;
    }
    public ResponseApi(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

}
