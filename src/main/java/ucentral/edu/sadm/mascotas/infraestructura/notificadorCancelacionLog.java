package ucentral.edu.sadm.mascotas.infraestructura;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import ucentral.edu.sadm.mascotas.dominio.notificadorCancelacion;

// TODO HU42: enviar correo o notificación real (hoy solo se registra en el log)
@ApplicationScoped
public class notificadorCancelacionLog implements notificadorCancelacion {
    private static final Logger LOG = Logger.getLogger(notificadorCancelacionLog.class);

    @Override
    public void notificar(String correoAdoptante, String nombreMascota) {
        LOG.infof("Notificación pendiente: avisar a %s que la publicación de %s fue cancelada por el dador",
                correoAdoptante, nombreMascota);
    }
}
