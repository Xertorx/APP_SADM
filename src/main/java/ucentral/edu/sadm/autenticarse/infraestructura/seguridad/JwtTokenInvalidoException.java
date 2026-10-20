package ucentral.edu.sadm.autenticarse.infraestructura.seguridad;

public class JwtTokenInvalidoException extends RuntimeException {

    public JwtTokenInvalidoException(Throwable cause) {
        super("El token JWT no es válido", cause);
    }
}
