package ucentral.edu.sadm.fundaciones;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ucentral.edu.sadm.fundaciones.dominio.fundacionRepositorio;
import ucentral.edu.sadm.usuarios.dominio.usuarioRepositorio;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class FundacionRecursosTest {

    @Inject
    usuarioRepositorio usuarioRepositorio;

    @Inject
    fundacionRepositorio fundacionRepositorio;

    @BeforeEach
    @Transactional
    void preparar() {
        fundacionRepositorio.deleteAll();
        usuarioRepositorio.deleteAll();
    }

    @Test
    @TestSecurity(user = "admin@example.com", roles = {"ADMINISTRADOR"})
    void registraFundacionPendienteYPermiteAprobacionAdministrativa() {
        Number fundacionId = given()
                .contentType("application/json")
                .body(datosFundacion())
                .when().post("/api/fundaciones/crear")
                .then()
                .statusCode(201)
                .body("data", notNullValue())
                .extract()
                .path("data");

        given()
                .when().get("/api/fundaciones")
                .then()
                .statusCode(200)
                .body("data[0].nombre", equalTo("Fundación Rescate"))
                .body("data[0].estado", equalTo("PENDIENTE_REVISION"));

        given()
                .when().patch("/api/fundaciones/" + fundacionId.longValue() + "/aprobar")
                .then()
                .statusCode(200);

        org.junit.jupiter.api.Assertions.assertEquals(
                "APROBADA",
                fundacionRepositorio.findById(fundacionId.longValue()).estado
        );
    }

    private Map<String, Object> datosFundacion() {
        return Map.of(
                "nombre", "Fundación Rescate",
                "nit", "900123456-7",
                "nombreRepresentante", "María",
                "apellidoRepresentante", "Pérez",
                "correo", "fundacion@example.com",
                "contraseña", "ClaveSegura123",
                "telefono", "+573001234567",
                "direccion", "Calle 12 # 34-56"
        );
    }
}
