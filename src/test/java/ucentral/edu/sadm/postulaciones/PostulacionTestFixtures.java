package ucentral.edu.sadm.postulaciones;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ucentral.edu.sadm.adoptantes.dominio.adoptante;
import ucentral.edu.sadm.adoptantes.dominio.adoptanteRepositorio;
import ucentral.edu.sadm.mascotas.dominio.mascota;
import ucentral.edu.sadm.mascotas.dominio.mascotaRepositorio;
import ucentral.edu.sadm.postulaciones.dominio.postulacionRepositorio;

@ApplicationScoped
public class PostulacionTestFixtures {
    @Inject
    adoptanteRepositorio adoptanteRepositorio;

    @Inject
    mascotaRepositorio mascotaRepositorio;

    @Inject
    postulacionRepositorio postulacionRepositorio;

    @Transactional
    public Datos preparar() {
        postulacionRepositorio.deleteAll();
        mascotaRepositorio.deleteAll();
        adoptanteRepositorio.deleteAll();

        adoptante primero = new adoptante();
        primero.correo = "primero@example.com";
        primero.tipo_usuario = "ADOPTANTE";
        adoptanteRepositorio.persistAndFlush(primero);

        adoptante segundo = new adoptante();
        segundo.correo = "segundo@example.com";
        segundo.tipo_usuario = "ADOPTANTE";
        adoptanteRepositorio.persistAndFlush(segundo);

        mascota disponible = new mascota();
        disponible.nombre = "Luna";
        disponible.especie = "Perro";
        disponible.raza = "Criollo";
        disponible.estado = "DISPONIBLE";
        mascotaRepositorio.persistAndFlush(disponible);

        mascota otraDisponible = new mascota();
        otraDisponible.nombre = "Nube";
        otraDisponible.estado = "DISPONIBLE";
        mascotaRepositorio.persistAndFlush(otraDisponible);

        mascota noDisponible = new mascota();
        noDisponible.nombre = "Max";
        noDisponible.estado = "PENDIENTE_APROBACION";
        mascotaRepositorio.persistAndFlush(noDisponible);

        return new Datos(primero.id, segundo.id, disponible.id, otraDisponible.id, noDisponible.id);
    }

    @Transactional
    public long contarPostulaciones() {
        return postulacionRepositorio.count();
    }

    public record Datos(
            Long adoptanteId,
            Long segundoAdoptanteId,
            Long mascotaId,
            Long otraMascotaId,
            Long mascotaNoDisponibleId
    ) {
    }
}
