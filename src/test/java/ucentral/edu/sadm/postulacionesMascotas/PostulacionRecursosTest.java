package ucentral.edu.sadm.postulacionesMascotas;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ucentral.edu.sadm.postulacionesMascotas.aplicaicon.postulacionServicio;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class PostulacionRecursosTest {
    private static final String BASE_PATH = "/api";

    @Inject
    PostulacionTestFixtures fixtures;

    @Inject
    postulacionServicio postulacionServicio;

    private PostulacionTestFixtures.Datos datos;

    @BeforeEach
    void preparar() {
        datos = fixtures.preparar();
    }

    @Test
    @TestSecurity(user = "primero@example.com", roles = {"USUARIO"})
    void crearRegistraEstadoFechaYpuntajeInicial() {
        given()
                .contentType("application/json")
                .body(Map.of("mascotaId", datos.mascotaId()))
                .when().post(BASE_PATH + "/postulaciones")
                .then()
                .statusCode(201)
                .body("data.id", notNullValue())
                .body("data.mascotaId", equalTo(datos.mascotaId().intValue()))
                .body("data.adoptanteId", equalTo(datos.adoptanteId().intValue()))
                .body("data.fecha", notNullValue())
                .body("data.estado", equalTo("Etapa 1 en curso"))
                .body("data.puntaje", equalTo(0));
    }

    @Test
    @TestSecurity(user = "primero@example.com", roles = {"USUARIO"})
    void mascotaInexistenteDevuelve404() {
        given()
                .contentType("application/json")
                .body(Map.of("mascotaId", 999999L))
                .when().post(BASE_PATH + "/postulaciones")
                .then()
                .statusCode(404)
                .body("data.codigo", equalTo("MASCOTA_NO_ENCONTRADA"));
    }

    @Test
    @TestSecurity(user = "primero@example.com", roles = {"USUARIO"})
    void mascotaNoDisponibleDevuelveConflictoConCodigo() {
        given()
                .contentType("application/json")
                .body(Map.of("mascotaId", datos.mascotaNoDisponibleId()))
                .when().post(BASE_PATH + "/postulaciones")
                .then()
                .statusCode(409)
                .body("data.codigo", equalTo("MASCOTA_NO_DISPONIBLE"));
    }

    @Test
    @TestSecurity(user = "primero@example.com", roles = {"USUARIO"})
    void requiereMascotaIdNumerico() {
        given()
                .contentType("application/json")
                .body("{}")
                .when().post(BASE_PATH + "/postulaciones")
                .then()
                .statusCode(400);
    }

    @Test
    @TestSecurity(user = "primero@example.com", roles = {"USUARIO"})
    void rechazaIdentificadorDeMascotaNoPositivo() {
        given()
                .contentType("application/json")
                .body(Map.of("mascotaId", 0))
                .when().post(BASE_PATH + "/postulaciones")
                .then()
                .statusCode(400);
    }

    @Test
    @TestSecurity(user = "primero@example.com", roles = {"USUARIO"})
    void ignoraDatosDeRespuestaEnviadosPorElCliente() {
        given()
                .contentType("application/json")
                .body(Map.of(
                        "mascotaId", datos.mascotaId(),
                        "id", 999,
                        "adoptanteId", 999,
                        "estado", "APROBADA",
                        "puntaje", 100
                ))
                .when().post(BASE_PATH + "/postulaciones")
                .then()
                .statusCode(201)
                .body("data.adoptanteId", equalTo(datos.adoptanteId().intValue()))
                .body("data.estado", equalTo("Etapa 1 en curso"))
                .body("data.puntaje", equalTo(0));
    }

    @Test
    @TestSecurity(user = "primero@example.com", roles = {"USUARIO"})
    void noPermitePostularDosVecesALaMismaMascota() {
        given()
                .contentType("application/json")
                .body(Map.of("mascotaId", datos.mascotaId()))
                .post(BASE_PATH + "/postulaciones")
                .then()
                .statusCode(201);

        given()
                .contentType("application/json")
                .body(Map.of("mascotaId", datos.mascotaId()))
                .post(BASE_PATH + "/postulaciones")
                .then()
                .statusCode(409)
                .body("data.codigo", equalTo("POSTULACION_DUPLICADA"));
    }

    @Test
    @TestSecurity(user = "primero@example.com", roles = {"USUARIO"})
    void adoptantesDistintosYmascotasDistintasPuedenPostularse() {
        postulacionServicio.crear(datos.adoptanteId(), datos.mascotaId());
        postulacionServicio.crear(datos.segundoAdoptanteId(), datos.mascotaId());
        postulacionServicio.crear(datos.adoptanteId(), datos.otraMascotaId());
    }

    @Test
    @TestSecurity(user = "primero@example.com", roles = {"USUARIO"})
    void resumenInformaDisponibilidadSinCrearRegistros() {
        given()
                .when().get(BASE_PATH + "/postulaciones/mascotas/" + datos.mascotaId() + "/resumen")
                .then()
                .statusCode(200)
                .body("data.puedePostularse", equalTo(true))
                .body("data.motivo", equalTo(null))
                .body("data.mascota.nombre", equalTo("Luna"));
        assertEquals(0, fixtures.contarPostulaciones());

        postulacionServicio.crear(datos.adoptanteId(), datos.mascotaId());

        given()
                .when().get(BASE_PATH + "/postulaciones/mascotas/" + datos.mascotaId() + "/resumen")
                .then()
                .statusCode(200)
                .body("data.puedePostularse", equalTo(false))
                .body("data.motivo", equalTo("POSTULACION_DUPLICADA"));
    }

    @Test
    @TestSecurity(user = "primero@example.com", roles = {"USUARIO"})
    void resumenExplicaCuandoMascotaNoEstaDisponible() {
        given()
                .when().get(BASE_PATH + "/postulaciones/mascotas/" + datos.mascotaNoDisponibleId() + "/resumen")
                .then()
                .statusCode(200)
                .body("data.puedePostularse", equalTo(false))
                .body("data.motivo", equalTo("MASCOTA_NO_DISPONIBLE"));
    }

    @Test
    @TestSecurity(user = "primero@example.com", roles = {"FUNDACION"})
    void rolIncorrectoDevuelve403() {
        given()
                .when().get(BASE_PATH + "/postulaciones/mascotas/" + datos.mascotaId() + "/resumen")
                .then()
                .statusCode(403);
    }

    @Test
    void sinAutenticacionDevuelve401() {
        given()
                .when().get(BASE_PATH + "/postulaciones/mascotas/" + datos.mascotaId() + "/resumen")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "primero@example.com", roles = {"USUARIO"})
    void dosSolicitudesConcurrentesCreanUnaSolaPostulacion() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            CompletableFuture<Integer> primera = CompletableFuture.supplyAsync(
                    () -> crearPostulacion(datos.mascotaId()), executor);
            CompletableFuture<Integer> segunda = CompletableFuture.supplyAsync(
                    () -> crearPostulacion(datos.mascotaId()), executor);

            int estadoPrimera = primera.get(15, TimeUnit.SECONDS);
            int estadoSegunda = segunda.get(15, TimeUnit.SECONDS);

            assertTrue((estadoPrimera == 201 && estadoSegunda == 409)
                    || (estadoPrimera == 409 && estadoSegunda == 201));
            assertEquals(1, fixtures.contarPostulaciones());
        } finally {
            executor.shutdownNow();
        }
    }

    private int crearPostulacion(Long mascotaId) {
        return given()
                .contentType("application/json")
                .body(Map.of("mascotaId", mascotaId))
                .post(BASE_PATH + "/postulaciones")
                .statusCode();
    }
}
