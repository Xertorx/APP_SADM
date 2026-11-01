package ucentral.edu.sadm.autenticarse.infraestructura.dto;

public record loginRespuesta(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
