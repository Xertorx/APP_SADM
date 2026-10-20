package ucentral.edu.sadm.autenticarse.infraestructura.seguridad;

import io.quarkus.security.identity.request.AuthenticationRequest;

import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationRequest implements AuthenticationRequest {

    private final String token;
    private final Map<String, Object> attributes = new HashMap<>();

    public JwtAuthenticationRequest(String token) {
        this.token = token;
    }

    public String token() {
        return token;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String name) {
        return (T) attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }
}
