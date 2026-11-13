package ucentral.edu.sadm.perfil.dominio.exception;

import ucentral.edu.sadm.common.infraestructure.BusinessException;

/** HU10: el usuario aún no registró su ubicación de referencia. */
public class UbicacionNoRegistradaException extends BusinessException {

    public UbicacionNoRegistradaException() {
        super(404, "Aún no registraste tu ubicación de referencia");
    }
}
