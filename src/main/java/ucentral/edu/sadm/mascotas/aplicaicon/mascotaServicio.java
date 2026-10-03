package ucentral.edu.sadm.mascotas.aplicaicon;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import ucentral.edu.sadm.adoptantes.dominio.adoptante;
import ucentral.edu.sadm.adoptantes.dominio.adoptanteRepositorio;
import ucentral.edu.sadm.mascotas.dominio.exception.DadorNoEncontradoException;
import ucentral.edu.sadm.mascotas.dominio.exception.FotoNoEncontradaException;
import ucentral.edu.sadm.mascotas.dominio.exception.FotosInvalidasException;
import ucentral.edu.sadm.mascotas.dominio.exception.MascotaNoEncontradaException;
import ucentral.edu.sadm.mascotas.dominio.fotoMascota;
import ucentral.edu.sadm.mascotas.dominio.fotoMascotaRepositorio;
import ucentral.edu.sadm.mascotas.dominio.mascota;
import ucentral.edu.sadm.mascotas.dominio.mascotaRepositorio;
import ucentral.edu.sadm.mascotas.infraestructura.dto.mascotaDetalleEntidad;
import ucentral.edu.sadm.mascotas.infraestructura.dto.mascotaEntidad;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class mascotaServicio {
    private static final Logger LOG = Logger.getLogger(mascotaServicio.class);

    private static final int MAX_FOTOS = 6;
    private static final long MAX_TAMANO_FOTO = 5L * 1024 * 1024;
    private static final Set<String> TIPOS_PERMITIDOS = Set.of("image/jpeg", "image/png");

    @Inject
    mascotaRepositorio mascotaRepositorio;
    @Inject
    fotoMascotaRepositorio fotoMascotaRepositorio;
    @Inject
    adoptanteRepositorio adoptanteRepositorio;

    @Transactional
    public Long crear(mascotaEntidad mascotaEntidad, List<FileUpload> fotos) {
        validarFotos(fotos);

        adoptante dador = adoptanteRepositorio.findById(mascotaEntidad.idDador());
        if (dador == null) {
            throw new DadorNoEncontradoException();
        }

        mascota mascota = new mascota(
                mascotaEntidad.nombre(),
                mascotaEntidad.especie(),
                mascotaEntidad.raza(),
                mascotaEntidad.edad(),
                mascotaEntidad.unidadEdad(),
                mascotaEntidad.tamano(),
                mascotaEntidad.sexo(),
                mascotaEntidad.historialSalud(),
                mascotaEntidad.ubicacion(),
                "PENDIENTE_APROBACION",
                new Date(),
                dador,
                mascotaEntidad.temperamento()
        );
        mascotaRepositorio.persist(mascota);

        int orden = 0;
        for (FileUpload foto : fotos) {
            fotoMascotaRepositorio.persist(leerFoto(mascota, foto, orden++));
        }

        return mascota.id;
    }

    public mascotaDetalleEntidad consultar(Long id) {
        mascota mascota = mascotaRepositorio.findById(id);
        if (mascota == null) {
            throw new MascotaNoEncontradaException();
        }

        List<Long> fotosIds = fotoMascotaRepositorio.listarPorMascota(mascota.id).stream()
                .map(f -> f.id)
                .toList();

        return new mascotaDetalleEntidad(
                mascota.id,
                mascota.nombre,
                mascota.especie,
                mascota.raza,
                mascota.edad,
                mascota.unidad_edad,
                mascota.tamano,
                mascota.sexo,
                mascota.temperamento,
                mascota.historial_salud,
                mascota.ubicacion,
                mascota.estado,
                mascota.fecha_publicacion,
                mascota.dador.id,
                fotosIds
        );
    }

    public fotoMascota consultarFoto(Long idFoto) {
        fotoMascota foto = fotoMascotaRepositorio.findById(idFoto);
        if (foto == null) {
            throw new FotoNoEncontradaException();
        }
        return foto;
    }

    private void validarFotos(List<FileUpload> fotos) {
        if (fotos == null || fotos.isEmpty()) {
            throw new FotosInvalidasException("Debes agregar al menos una foto de la mascota");
        }
        if (fotos.size() > MAX_FOTOS) {
            throw new FotosInvalidasException("No puedes publicar más de " + MAX_FOTOS + " fotos");
        }
        for (FileUpload foto : fotos) {
            String tipo = foto.contentType();
            if (tipo == null || !TIPOS_PERMITIDOS.contains(tipo)) {
                throw new FotosInvalidasException("Las fotos deben ser JPG o PNG");
            }
            if (foto.size() > MAX_TAMANO_FOTO) {
                throw new FotosInvalidasException("Cada foto debe pesar máximo 5 MB");
            }
        }
    }

    private fotoMascota leerFoto(mascota mascota, FileUpload foto, int orden) {
        try {
            byte[] datos = Files.readAllBytes(foto.uploadedFile());
            return new fotoMascota(
                    mascota,
                    foto.fileName(),
                    foto.contentType(),
                    foto.size(),
                    datos,
                    orden
            );
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer la foto " + foto.fileName(), e);
        }
    }
}
