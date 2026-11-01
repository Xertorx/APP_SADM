package ucentral.edu.sadm.mascotas;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ucentral.edu.sadm.postulacionesMascotas.PostulacionTestFixtures;
import ucentral.edu.sadm.postulacionesMascotas.aplicaicon.postulacionServicio;

import static io.restassured.http.ContentType.MULTIPART;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
class MascotaRecursosTest {
    private static final String BASE_PATH = "/api/mascotas";

    @Inject
    PostulacionTestFixtures fixtures;

    @Inject
    postulacionServicio postulacionServicio;

    private PostulacionTestFixtures.Datos datos;

    @BeforeEach
    void preparar() {
        datos = fixtures.preparar();
        fixtures.asignarDador(datos.mascotaId(), datos.adoptanteId());
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

    @Test
    @TestSecurity(user = "primero@example.com", roles = {"USUARIO"})
    void consultarMisPublicacionesDevuelveSoloMascotasDelDadorAutenticado() {
        given()
                .when().get(BASE_PATH + "/mis-publicaciones")
                .then()
                .statusCode(200)
                .body("data", hasSize(1))
                .body("data[0].nombre", equalTo("Luna"));
    }

    @Test
    @TestSecurity(user = "primero@example.com", roles = {"USUARIO"})
    void consultarParaEditarPrecargaDatosYAdviertePostulacionesActivas() {
        postulacionServicio.crear(datos.segundoAdoptanteId(), datos.mascotaId());

        given()
                .when().get(BASE_PATH + "/" + datos.mascotaId() + "/editar")
                .then()
                .statusCode(200)
                .body("data.mascota.nombre", equalTo("Luna"))
                .body("data.mascota.idDador", equalTo(datos.adoptanteId().intValue()))
                .body("data.tienePostulacionesActivas", equalTo(true))
                .body("data.advertencia", equalTo(
                        "Esta mascota tiene postulaciones activas. Confirma antes de guardar los cambios."));
    }

    @Test
    @TestSecurity(user = "primero@example.com", roles = {"USUARIO"})
    void actualizarGuardaDatosCuandoNoSeEnvianFotos() {
        given()
                .contentType(MULTIPART)
                .multiPart("datos", datosValidos("Luna actualizada"), "application/json")
                .when().put(BASE_PATH + "/" + datos.mascotaId())
                .then()
                .statusCode(200);

        given()
                .when().get(BASE_PATH + "/" + datos.mascotaId())
                .then()
                .statusCode(200)
                .body("nombre", equalTo("Luna actualizada"));
    }

    @Test
    @TestSecurity(user = "segundo@example.com", roles = {"USUARIO"})
    void soloElDadorPropietarioPuedeEditar() {
        given()
                .when().get(BASE_PATH + "/" + datos.mascotaId() + "/editar")
                .then()
                .statusCode(403);

        given()
                .contentType(MULTIPART)
                .multiPart("datos", datosValidos("No autorizada"), "application/json")
                .when().put(BASE_PATH + "/" + datos.mascotaId())
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "primero@example.com", roles = {"USUARIO"})
    void actualizarAplicaLasValidacionesDePublicacion() {
        given()
                .contentType(MULTIPART)
                .multiPart("datos", datosValidos("X"), "application/json")
                .when().put(BASE_PATH + "/" + datos.mascotaId())
                .then()
                .statusCode(400);
    }

    private String datosValidos(String nombre) {
        return """
                {
                  "nombre": "%s",
                  "especie": "PERRO",
                  "raza": "Criollo",
                  "edad": 2,
                  "unidadEdad": "ANIOS",
                  "tamano": "MEDIANO",
                  "sexo": "HEMBRA",
                  "temperamento": ["SOCIABLE"],
                  "historialSalud": "Vacunada",
                  "ubicacion": "Bogotá"
                }
                """.formatted(nombre);
    }
}
