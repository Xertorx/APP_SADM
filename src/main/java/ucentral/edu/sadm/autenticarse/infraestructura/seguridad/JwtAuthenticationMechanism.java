package ucentral.edu.sadm.autenticarse.infraestructura.seguridad;

import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Set;

@ApplicationScoped
public class JwtAuthenticationMechanism implements HttpAuthenticationMechanism {

    @Override
    public Uni<SecurityIdentity> authenticate(
            RoutingContext context,
            IdentityProviderManager identityProviderManager
    ) {
        String authorization = context.request().getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Uni.createFrom().nullItem();
        }

        String token = authorization.substring("Bearer ".length()).trim();
        if (token.isEmpty()) {
            return Uni.createFrom().failure(new io.quarkus.security.AuthenticationFailedException());
        }
        return identityProviderManager.authenticate(new JwtAuthenticationRequest(token));
    }

    @Override
    public Uni<ChallengeData> getChallenge(RoutingContext context) {
        return Uni.createFrom().item(new ChallengeData(401, "WWW-Authenticate", "Bearer"));
    }

    @Override
    public Set<Class<? extends io.quarkus.security.identity.request.AuthenticationRequest>> getCredentialTypes() {
        return Set.of(JwtAuthenticationRequest.class);
    }
}
