package ucentral.edu.sadm.seleccion;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ucentral.edu.sadm.postulacionesMascotas.PostulacionTestFixtures;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
class ConfiguracionSeleccionRecursosTest {
    private static final String CONFIGURACION_PATH = "/api/mascotas/%d/configuracion-seleccion";

    @Inject
    PostulacionTestFixtures fixtures;

    private PostulacionTestFixtures.Datos datos;

    @BeforeEach
    void preparar() {
        datos = fixtures.preparar();
        fixtures.asignarDador(datos.mascotaId(), datos.adoptanteId());
        fixtures.marcarBorradorConfiguracion(datos.mascotaId());
    }

    @Test
    @TestSecurity(user = "primero@example.com", roles = {"USUARIO"})
    void guardaFinalizaYPublicaSoloConDistribucionCompleta() {
        String path = CONFIGURACION_PATH.formatted(datos.mascotaId());

        given()
                .when().get(path)
                .then()
                .statusCode(200)
                .body("data.definitiva", equalTo(false))
                .body("data.puntosPendientes", equalTo(100))
                .body("data.preguntas.elementos", hasSize(0));

        given()
                .when().get("/api/mascotas/" + datos.mascotaId())
                .then()
                .statusCode(404);

        given()
                .when().post("/api/mascotas/" + datos.mascotaId() + "/publicar")
                .then()
                .statusCode(409)
                .body("data.codigo", equalTo("CONFIGURACION_NO_FINALIZADA"));

        Number preguntaId = given()
                .contentType("application/json")
                .body(configuracionValida())
                .when().put(path)
                .then()
                .statusCode(200)
                .body("data.puntajeDistribuido", equalTo(100))
                .body("data.puntosPendientes", equalTo(0))
                .body("data.preguntas.elementos[0].id", org.hamcrest.Matchers.notNullValue())
                .extract().path("data.preguntas.elementos[0].id");

        String preguntaActualizada = configuracionValida().replace(
                "\"texto\": \"¿Cuántas horas estaría sola la mascota?\"",
                "\"id\": " + preguntaId + ", \"texto\": \"¿Cuántas horas permanecería sola?\""
        );
        given()
                .contentType("application/json")
                .body(preguntaActualizada)
                .when().put(path)
                .then()
                .statusCode(200)
                .body("data.preguntas.elementos[0].id", equalTo(preguntaId.intValue()))
                .body("data.preguntas.elementos[0].texto", equalTo("¿Cuántas horas permanecería sola?"));

        given()
                .when().post(path + "/finalizar")
                .then()
                .statusCode(200)
                .body("data.definitiva", equalTo(true));

        given()
                .when().post("/api/mascotas/" + datos.mascotaId() + "/publicar")
                .then()
                .statusCode(200);

        given()
                .contentType("application/json")
                .body(configuracionValida())
                .when().put(path)
                .then()
                .statusCode(409)
                .body("data.codigo", equalTo("CONFIGURACION_NO_EDITABLE"));
    }

    @Test
    @TestSecurity(user = "primero@example.com", roles = {"USUARIO"})
    void rechazaFinalizarConEtapaVacia() {
        String path = CONFIGURACION_PATH.formatted(datos.mascotaId());
        String incompleta = configuracionValida().replace(
                "\"criterios\": [{\"descripcion\": \"Espacio seguro y adecuado\", \"puntajeMaximo\": 25}]",
                "\"criterios\": []"
        );

        given()
                .contentType("application/json")
                .body(incompleta)
                .when().put(path)
                .then()
                .statusCode(200);

        given()
                .when().post(path + "/finalizar")
                .then()
                .statusCode(400)
                .body("data.codigo", equalTo("ETAPA_SIN_CRITERIOS"));

        given()
                .when().get(path)
                .then()
                .statusCode(200);
    }

    @Test
    @TestSecurity(user = "primero@example.com", roles = {"USUARIO"})
    void rechazaPuntajesDeElementosQueSuperanElMaximoDeSuEtapa() {
        String request = configuracionValida().replace("\"puntaje\": 10", "\"puntaje\": 31");

        given()
                .contentType("application/json")
                .body(request)
                .when().put(CONFIGURACION_PATH.formatted(datos.mascotaId()))
                .then()
                .statusCode(400)
                .body("data.codigo", equalTo("PUNTAJE_SUPERA_ETAPA"));
    }

    @Test
    @TestSecurity(user = "segundo@example.com", roles = {"USUARIO"})
    void solamenteElDadorPropietarioPuedeConsultarLaConfiguracion() {
        String path = CONFIGURACION_PATH.formatted(datos.mascotaId());
        given()
                .when().get(path)
                .then()
                .statusCode(403)
                .body("data.codigo", equalTo("MASCOTA_NO_AUTORIZADA"));

        given()
                .contentType("application/json")
                .body(configuracionValida())
                .when().put(path)
                .then()
                .statusCode(403)
                .body("data.codigo", equalTo("MASCOTA_NO_AUTORIZADA"));
    }

    private String configuracionValida() {
        return """
                {
                  "preguntas": {
                    "puntajeMaximo": 30,
                    "elementos": [{
                      "texto": "¿Cuántas horas estaría sola la mascota?",
                      "tipoRespuesta": "SELECCION_UNICA",
                      "puntaje": 10,
                      "opciones": ["Menos de 2", "De 2 a 5", "Más de 5"]
                    }]
                  },
                  "documentos": {
                    "puntajeMaximo": 20,
                    "elementos": [{
                      "nombre": "Documento de identidad",
                      "descripcion": "Copia legible del documento",
                      "obligatorio": true,
                      "puntajeMaximo": 20
                    }]
                  },
                  "entrevista": {
                    "puntajeMaximo": 25,
                    "criterios": [{"descripcion": "Experiencia previa", "puntajeMaximo": 25}]
                  },
                  "visitaDomiciliaria": {
                    "puntajeMaximo": 25,
                    "criterios": [{"descripcion": "Espacio seguro y adecuado", "puntajeMaximo": 25}]
                  }
                }
                """;
    }
}
