package ucentral.edu.sadm.postulacionesMascotas.dominio.exception;

import ucentral.edu.sadm.common.infraestructure.BusinessException;

public class PostulacionException extends BusinessException {

    public PostulacionException(int status, String codigo, String mensaje, String detalle) {
        super(status, codigo, mensaje, detalle);
    }
}
