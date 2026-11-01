package ucentral.edu.sadm.mascotas.dominio.exception;

import ucentral.edu.sadm.common.infraestructure.BusinessException;

public class FotosInvalidasException extends BusinessException {

  public FotosInvalidasException(String message) {
    super(400, message);
  }
}
