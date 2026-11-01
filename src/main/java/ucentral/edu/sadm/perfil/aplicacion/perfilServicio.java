package ucentral.edu.sadm.perfil.aplicacion;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import ucentral.edu.sadm.common.service.PasswordService;
import ucentral.edu.sadm.perfil.dominio.exception.FotoPerfilInvalidaException;
import ucentral.edu.sadm.perfil.dominio.exception.PerfilInvalidoException;
import ucentral.edu.sadm.perfil.dominio.exception.PerfilNoEncontradoException;
import ucentral.edu.sadm.perfil.dominio.fotoPerfil;
import ucentral.edu.sadm.perfil.dominio.fotoPerfilRepositorio;
import ucentral.edu.sadm.perfil.infraestructura.dto.perfilActualizacionEntidad;
import ucentral.edu.sadm.perfil.infraestructura.dto.perfilEntidad;
import ucentral.edu.sadm.perfil.infraestructura.dto.perfilPublicoEntidad;
import ucentral.edu.sadm.usuarios.dominio.usuario;
import ucentral.edu.sadm.usuarios.dominio.usuarioRepositorio;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Set;
import java.util.regex.Pattern;

@ApplicationScoped
@Transactional
public class perfilServicio {

    private static final long MAX_TAMANO_FOTO = 5L * 1024 * 1024;
    private static final Set<String> TIPOS_PERMITIDOS = Set.of("image/jpeg", "image/png");
    private static final Pattern FORMATO_NUEVA = Pattern.compile("^(?=.*[A-Z])(?=.*\\d).{8,72}$");

    @Inject
    usuarioRepositorio usuarioRepositorio;
    @Inject
    fotoPerfilRepositorio fotoPerfilRepositorio;
    @Inject
    PasswordService passwordService;

    public perfilEntidad consultar(String correo) {
        usuario usuario = buscarUsuario(correo);
        fotoPerfil foto = fotoPerfilRepositorio.findByUsuarioId(usuario.id);
        return new perfilEntidad(
                usuario.id,
                usuario.nombre,
                usuario.apellido,
                usuario.correo,
                usuario.telefono,
                usuario.direccion,
                foto != null,
                foto == null ? null : foto.actualizada_en
        );
    }

    /**
     * Una sola transacción: primero se valida todo (foto y contraseña) y recién después se
     * modifica. Si algo lanza excepción, Quarkus hace rollback y no se guarda nada.
     */
    public void actualizar(perfilActualizacionEntidad datos, FileUpload foto, String correo) {
        usuario usuario = buscarUsuario(correo);
        boolean quitarFoto = Boolean.TRUE.equals(datos.quitarFoto());

        if (foto != null && quitarFoto) {
            throw new PerfilInvalidoException("No puedes subir y quitar la foto al mismo tiempo");
        }
        byte[] bytesFoto = foto == null ? null : validarYLeerFoto(foto);
        String nuevoHash = validarCambioContrasena(datos, usuario);

        usuario.nombre = datos.nombre().trim();
        usuario.apellido = datos.apellido().trim();
        usuario.telefono = datos.telefono().trim();
        usuario.direccion = datos.ubicacion().trim();
        if (nuevoHash != null) {
            usuario.passwordHash = nuevoHash;
        }

        if (bytesFoto != null) {
            guardarFoto(usuario, foto, bytesFoto);
        } else if (quitarFoto) {
            fotoPerfilRepositorio.delete("usuario.id", usuario.id);
        }
    }

    public fotoPerfil consultarFoto(String correo) {
        usuario usuario = buscarUsuario(correo);
        fotoPerfil foto = fotoPerfilRepositorio.findByUsuarioId(usuario.id);
        if (foto == null) {
            throw new PerfilNoEncontradoException("No tienes foto de perfil");
        }
        return foto;
    }

    /** Solo datos públicos del dador (HU11): sin correo, teléfono, dirección ni identificación. */
    public perfilPublicoEntidad consultarPublico(Long usuarioId) {
        usuario usuario = buscarUsuarioPorId(usuarioId);
        return new perfilPublicoEntidad(
                usuario.id,
                usuario.nombre,
                usuario.apellido,
                fotoPerfilRepositorio.findByUsuarioId(usuario.id) != null,
                usuario.fecha_registro
        );
    }

    public fotoPerfil consultarFotoPublica(Long usuarioId) {
        buscarUsuarioPorId(usuarioId);
        fotoPerfil foto = fotoPerfilRepositorio.findByUsuarioId(usuarioId);
        if (foto == null) {
            throw new PerfilNoEncontradoException("El usuario no tiene foto de perfil");
        }
        return foto;
    }

    private usuario buscarUsuarioPorId(Long usuarioId) {
        usuario usuario = usuarioRepositorio.findById(usuarioId);
        if (usuario == null) {
            throw new PerfilNoEncontradoException("El usuario no existe");
        }
        return usuario;
    }

    private usuario buscarUsuario(String correo) {
        usuario usuario = usuarioRepositorio.findByEmail(correo);
        if (usuario == null) {
            throw new PerfilNoEncontradoException();
        }
        return usuario;
    }

    /** Devuelve el hash nuevo, o null si el bloque de contraseña no se usó. */
    private String validarCambioContrasena(perfilActualizacionEntidad datos, usuario usuario) {
        String actual = datos.contrasenaActual();
        String nueva = datos.contrasenaNueva();
        String confirmar = datos.confirmarContrasena();

        if (estaVacia(actual) && estaVacia(nueva) && estaVacia(confirmar)) {
            return null;
        }
        if (estaVacia(actual)) {
            throw new PerfilInvalidoException("Debes ingresar tu contraseña actual para cambiar la contraseña");
        }
        if (estaVacia(nueva) || !FORMATO_NUEVA.matcher(nueva).matches()) {
            throw new PerfilInvalidoException(
                    "La nueva contraseña debe tener entre 8 y 72 caracteres, una mayúscula y un número");
        }
        if (!nueva.equals(confirmar)) {
            throw new PerfilInvalidoException("Las contraseñas no coinciden");
        }
        if (!passwordService.verificar(actual, usuario.passwordHash)) {
            throw new PerfilInvalidoException("La contraseña actual es incorrecta");
        }
        if (nueva.equals(actual)) {
            throw new PerfilInvalidoException("La nueva contraseña debe ser diferente a la actual");
        }
        return passwordService.hash(nueva);
    }

    private byte[] validarYLeerFoto(FileUpload foto) {
        String tipo = foto.contentType();
        if (tipo == null || !TIPOS_PERMITIDOS.contains(tipo)) {
            throw new FotoPerfilInvalidaException("La foto debe ser JPG o PNG");
        }
        if (foto.size() > MAX_TAMANO_FOTO) {
            throw new FotoPerfilInvalidaException("La foto no puede superar 5 MB");
        }
        byte[] datos;
        try {
            datos = Files.readAllBytes(foto.uploadedFile());
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer la foto " + foto.fileName(), e);
        }
        // El Content-Type lo manda el cliente; los primeros bytes confirman que sea imagen real.
        if (!esJpeg(datos) && !esPng(datos)) {
            throw new FotoPerfilInvalidaException("La foto debe ser JPG o PNG");
        }
        return datos;
    }

    private void guardarFoto(usuario usuario, FileUpload archivo, byte[] bytes) {
        fotoPerfil foto = fotoPerfilRepositorio.findByUsuarioId(usuario.id);
        if (foto == null) {
            foto = new fotoPerfil();
            foto.usuario = usuario;
        }
        foto.nombre_archivo = archivo.fileName();
        foto.tipo_contenido = esPng(bytes) ? "image/png" : "image/jpeg";
        foto.tamano_bytes = (long) bytes.length;
        foto.datos = bytes;
        foto.actualizada_en = Instant.now();
        fotoPerfilRepositorio.persist(foto);
    }

    private static boolean esJpeg(byte[] b) {
        return b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF;
    }

    private static boolean esPng(byte[] b) {
        return b.length >= 4 && (b[0] & 0xFF) == 0x89 && b[1] == 0x50 && b[2] == 0x4E && b[3] == 0x47;
    }

    private static boolean estaVacia(String valor) {
        return valor == null || valor.isEmpty();
    }
}
