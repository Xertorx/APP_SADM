package ucentral.edu.sadm.postulaciones.aplicaicon;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ucentral.edu.sadm.adoptantes.dominio.adoptante;
import ucentral.edu.sadm.adoptantes.dominio.adoptanteRepositorio;
import ucentral.edu.sadm.mascotas.dominio.fotoMascotaRepositorio;
import ucentral.edu.sadm.mascotas.dominio.mascota;
import ucentral.edu.sadm.mascotas.dominio.mascotaRepositorio;
import ucentral.edu.sadm.postulaciones.dominio.postulacion;
import ucentral.edu.sadm.postulaciones.dominio.postulacionRepositorio;
import ucentral.edu.sadm.postulaciones.dominio.exception.PostulacionException;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

class PostulacionServicioTest {
    @Mock
    mascotaRepositorio mascotaRepositorio;

    @Mock
    adoptanteRepositorio adoptanteRepositorio;

    @Mock
    fotoMascotaRepositorio fotoMascotaRepositorio;

    @Mock
    postulacionRepositorio postulacionRepositorio;

    private AutoCloseable mocks;
    private postulacionServicio servicio;
    private adoptante adoptante;
    private mascota mascota;

    @BeforeEach
    void preparar() {
        mocks = MockitoAnnotations.openMocks(this);
        servicio = new postulacionServicio();
        servicio.mascotaRepositorio = mascotaRepositorio;
        servicio.adoptanteRepositorio = adoptanteRepositorio;
        servicio.fotoMascotaRepositorio = fotoMascotaRepositorio;
        servicio.postulacionRepositorio = postulacionRepositorio;

        adoptante = new adoptante();
        adoptante.id = 7L;
        mascota = new mascota();
        mascota.id = 12L;
        mascota.estado = "Disponible";
        when(mascotaRepositorio.findById(12L)).thenReturn(mascota);
        when(adoptanteRepositorio.findById(7L)).thenReturn(adoptante);
        when(postulacionRepositorio.buscarPorAdoptanteYMascota(7L, 12L)).thenReturn(null);

        AtomicLong ids = new AtomicLong(30L);
        doAnswer(invocation -> {
            postulacion creada = invocation.getArgument(0);
            creada.id = ids.getAndIncrement();
            return null;
        }).when(postulacionRepositorio).persistAndFlush(any(postulacion.class));
    }

    @AfterEach
    void cerrarMocks() throws Exception {
        mocks.close();
    }

    @Test
    void crearAsignaFechaEstadoYpuntajeInicial() {
        var creada = servicio.crear(7L, 12L);

        assertNotNull(creada.fecha());
        assertEquals("Etapa 1 en curso", creada.estado());
        assertEquals(0, creada.puntaje());
        assertEquals(7L, creada.adoptanteId());
        assertEquals(12L, creada.mascotaId());
    }

    @Test
    void crearRechazaMascotaNoDisponible() {
        mascota.estado = "PENDIENTE_APROBACION";

        PostulacionException excepcion = assertThrows(
                PostulacionException.class,
                () -> servicio.crear(7L, 12L)
        );

        assertEquals("MASCOTA_NO_DISPONIBLE", excepcion.getErrorCode());
    }

    @Test
    void crearRechazaPostulacionDuplicada() {
        when(postulacionRepositorio.buscarPorAdoptanteYMascota(7L, 12L))
                .thenReturn(new postulacion());

        PostulacionException excepcion = assertThrows(
                PostulacionException.class,
                () -> servicio.crear(7L, 12L)
        );

        assertEquals("POSTULACION_DUPLICADA", excepcion.getErrorCode());
    }
}
