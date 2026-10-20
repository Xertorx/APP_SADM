package ucentral.edu.sadm.mascotas;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ucentral.edu.sadm.postulacionesMascotas.PostulacionTestFixtures;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
class MascotaRecursosTest {
    private static final String BASE_PATH = "/api/mascotas";

    @Inject
    PostulacionTestFixtures fixtures;

    @BeforeEach
    void preparar() {
        fixtures.preparar();
    }

    @Test
    void consultarTodasDevuelveMascotasDeTodosLosEstados() {
        given()
                .when().get(BASE_PATH)
                .then()
                .statusCode(200)
                .body("status", equalTo(200))
                .body("data", hasSize(3))
                .body("data.nombre", containsInAnyOrder("Luna", "Nube", "Max"))
                .body("data.estado", containsInAnyOrder(
                        "DISPONIBLE", "DISPONIBLE", "PENDIENTE_APROBACION"));
    }
}
