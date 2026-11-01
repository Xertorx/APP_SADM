package ucentral.edu.sadm.autenticarse.infraestructura.seguridad;

import io.jsonwebtoken.Claims;
import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import ucentral.edu.sadm.usuarios.dominio.rolUsuario;

import java.security.Principal;

@ApplicationScoped
public class JwtIdentityProvider implements IdentityProvider<JwtAuthenticationRequest> {

    private final jwtServicio jwtServicio;

    public JwtIdentityProvider(jwtServicio jwtServicio) {
        this.jwtServicio = jwtServicio;
    }

    @Override
    public Class<JwtAuthenticationRequest> getRequestType() {
        return JwtAuthenticationRequest.class;
    }

    @Override
    public io.smallrye.mutiny.Uni<SecurityIdentity> authenticate(
            JwtAuthenticationRequest request,
            AuthenticationRequestContext context
    ) {
        return context.runBlocking(() -> {
            try {
                Claims claims = jwtServicio.validar(request.token());
                String correo = claims.getSubject();
                String rol = claims.get("role", String.class);
                Long userId = claims.get("userId", Long.class);
                rolUsuario.valueOf(rol);
                if (correo == null || correo.isBlank() || userId == null || userId <= 0
                        || claims.getExpiration() == null) {
                    throw new JwtTokenInvalidoException(new IllegalArgumentException("Faltan claims requeridos"));
                }
                Principal principal = () -> correo;
                return QuarkusSecurityIdentity.builder()
                        .setPrincipal(principal)
                        .addRole(rol)
                        .addAttribute("userId", userId)
                        .build();
            } catch (RuntimeException exception) {
                throw new AuthenticationFailedException(exception);
            }
        });
    }
}
