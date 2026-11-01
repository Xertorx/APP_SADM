package ucentral.edu.sadm.mascotas.dominio;

/**
 * Punto de enchufe para avisar a un candidato de que el dador canceló el proceso
 * al eliminar la publicación. El servicio depende solo de esta interfaz.
 */
public interface notificadorCancelacion {

    void notificar(String correoAdoptante, String nombreMascota);
}
