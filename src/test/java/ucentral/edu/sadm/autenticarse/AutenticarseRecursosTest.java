package ucentral.edu.sadm.autenticarse;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ucentral.edu.sadm.common.service.PasswordService;
import ucentral.edu.sadm.fundaciones.dominio.fundacionRepositorio;
import ucentral.edu.sadm.usuarios.dominio.rolUsuario;
import ucentral.edu.sadm.usuarios.dominio.usuario;
import ucentral.edu.sadm.usuarios.dominio.usuarioRepositorio;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class AutenticarseRecursosTest {

    @Inject
    usuarioRepositorio usuarioRepositorio;

    @Inject
    fundacionRepositorio fundacionRepositorio;

    @Inject
    PasswordService passwordService;

    @BeforeEach
    @Transactional
    void preparar() {
        fundacionRepositorio.deleteAll();
        usuarioRepositorio.deleteAll();
        usuario usuario = new usuario();
        usuario.nombre = "Usuario";
        usuario.apellido = "Prueba";
        usuario.correo = "usuario@example.com";
        usuario.passwordHash = passwordService.hash("ClaveSegura123");
        usuario.rol = rolUsuario.USUARIO;
        usuario.estado = "ACTIVO";
        usuarioRepositorio.persistAndFlush(usuario);
    }

    @Test
    void loginEmiteJwtYPermiteValidarLaSesion() {
        String token = given()
                .contentType("application/json")
                .body(Map.of("correo", "usuario@example.com", "contraseña", "ClaveSegura123"))
                .when().post("/api/autenticacion/login")
                .then()
                .statusCode(200)
                .body("data.tokenType", equalTo("Bearer"))
                .body("data.expiresIn", equalTo(900))
                .body("data.accessToken", notNullValue())
                .extract()
                .path("data.accessToken");

        given()
                .header("Authorization", "Bearer " + token)
                .when().post("/api/autenticacion/sesion")
                .then()
                .statusCode(200)
                .body("message", equalTo("Sesión activa"));
    }

    @Test
    void credencialesInvalidasNoInicianSesion() {
        given()
                .contentType("application/json")
                .body(Map.of("correo", "usuario@example.com", "contraseña", "incorrecta"))
                .when().post("/api/autenticacion/login")
                .then()
                .statusCode(401);
    }

    @Test
    void registroAsignaRolComunYAlmacenaLaContrasenaHasheada() {
        given()
                .contentType("application/json")
                .body(Map.of(
                        "nombre", "Nuevo",
                        "apellido", "Usuario",
                        "identificacion", 9_999_999_999L,
                        "fecha_nacimiento", "1990-01-01T00:00:00.000Z",
                        "correo", "nuevo@example.com",
                        "contraseña", "ClaveSegura123",
                        "telefono", "+573001234567",
                        "direccion", "Calle 12 # 34-56"
                ))
                .when().post("/api/usuarios/crear")
                .then()
                .statusCode(201)
                .body("data", notNullValue());

        usuario registrado = usuarioRepositorio.findByEmail("nuevo@example.com");
        assertNotNull(registrado);
        assertEquals(9_999_999_999L, registrado.identificacion);
        assertEquals(rolUsuario.USUARIO, registrado.rol);
        assertEquals("ACTIVO", registrado.estado);
        org.junit.jupiter.api.Assertions.assertNotEquals("ClaveSegura123", registrado.passwordHash);
    }

    @Test
    void registroRechazaIdentificacionConMasDeDiezDigitos() {
        given()
                .contentType("application/json")
                .body(Map.of(
                        "nombre", "Nuevo",
                        "apellido", "Usuario",
                        "identificacion", 10_000_000_000L,
                        "fecha_nacimiento", "1990-01-01T00:00:00.000Z",
                        "correo", "nuevo@example.com",
                        "contraseña", "ClaveSegura123",
                        "telefono", "+573001234567",
                        "direccion", "Calle 12 # 34-56"
                ))
                .when().post("/api/usuarios/crear")
                .then()
                .statusCode(400);
    }

    @Test
    void usuarioNoPuedeConsultarLaListaAdministrativa() {
        String token = given()
                .contentType("application/json")
                .body(Map.of("correo", "usuario@example.com", "contraseña", "ClaveSegura123"))
                .post("/api/autenticacion/login")
                .then()
                .statusCode(200)
                .extract()
                .path("data.accessToken");

        assertNotNull(token);
        given()
                .header("Authorization", "Bearer " + token)
                .when().get("/api/usuarios")
                .then()
                .statusCode(403);
    }

    @Test
    void rechazaUnJwtManipulado() {
        given()
                .header("Authorization", "Bearer not-a-valid-jwt")
                .when().post("/api/autenticacion/sesion")
                .then()
                .statusCode(401);
    }
}
