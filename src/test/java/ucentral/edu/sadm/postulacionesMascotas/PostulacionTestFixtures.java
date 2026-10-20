package ucentral.edu.sadm.postulacionesMascotas;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ucentral.edu.sadm.usuarios.dominio.usuario;
import ucentral.edu.sadm.usuarios.dominio.usuarioRepositorio;
import ucentral.edu.sadm.usuarios.dominio.rolUsuario;
import ucentral.edu.sadm.fundaciones.dominio.fundacionRepositorio;
import ucentral.edu.sadm.mascotas.dominio.mascota;
import ucentral.edu.sadm.mascotas.dominio.mascotaRepositorio;
import ucentral.edu.sadm.postulacionesMascotas.dominio.postulacionRepositorio;

@ApplicationScoped
public class PostulacionTestFixtures {
    @Inject
    usuarioRepositorio usuarioRepositorio;

    @Inject
    fundacionRepositorio fundacionRepositorio;

    @Inject
    mascotaRepositorio mascotaRepositorio;

    @Inject
    postulacionRepositorio postulacionRepositorio;

    @Transactional
    public Datos preparar() {
        postulacionRepositorio.deleteAll();
        mascotaRepositorio.deleteAll();
        fundacionRepositorio.deleteAll();
        usuarioRepositorio.deleteAll();

        usuario primero = new usuario();
        primero.correo = "primero@example.com";
        primero.rol = rolUsuario.USUARIO;
        primero.estado = "ACTIVO";
        primero.passwordHash = "hash";
        usuarioRepositorio.persistAndFlush(primero);

        usuario segundo = new usuario();
        segundo.correo = "segundo@example.com";
        segundo.rol = rolUsuario.USUARIO;
        segundo.estado = "ACTIVO";
        segundo.passwordHash = "hash";
        usuarioRepositorio.persistAndFlush(segundo);

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
