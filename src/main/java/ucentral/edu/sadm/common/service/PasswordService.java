package ucentral.edu.sadm.common.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PasswordService {

    public String hash(String password) {
        return BcryptUtil.bcryptHash(password);
    }

    public boolean verificar(String password, String passwordHash) {
        return BcryptUtil.matches(password, passwordHash);
    }
}