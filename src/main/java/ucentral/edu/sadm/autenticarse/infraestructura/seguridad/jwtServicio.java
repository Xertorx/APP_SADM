package ucentral.edu.sadm.autenticarse.infraestructura.seguridad;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import ucentral.edu.sadm.usuarios.dominio.usuario;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@ApplicationScoped
public class jwtServicio {

    @ConfigProperty(name = "app.jwt.secret")
    String secret;

    @ConfigProperty(name = "app.jwt.expiration-seconds")
    long expirationSeconds;

    public TokenEmitido emitir(usuario usuario) {
        if (expirationSeconds <= 0) {
            throw new IllegalStateException("JWT_EXPIRATION_SECONDS debe ser mayor que cero");
        }
        Instant ahora = Instant.now();
        Instant expiracion = ahora.plusSeconds(expirationSeconds);
        String token = Jwts.builder()
                .issuer("APP_SADM")
                .subject(usuario.correo)
                .claim("userId", usuario.id)
                .claim("role", usuario.rol.name())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(expiracion))
                .signWith(signingKey(), Jwts.SIG.HS256)
                .compact();
        return new TokenEmitido(token, expirationSeconds);
    }

    public Claims validar(String token) {
        try {
            return Jwts.parser()
                    .requireIssuer("APP_SADM")
                    .verifyWith(signingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException exception) {
            throw new JwtTokenInvalidoException(exception);
        }
    }

    private SecretKey signingKey() {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET debe contener al menos 32 bytes");
        }
        return Keys.hmacShaKeyFor(bytes);
    }

    public record TokenEmitido(String token, long expiraEnSegundos) {
    }
}
