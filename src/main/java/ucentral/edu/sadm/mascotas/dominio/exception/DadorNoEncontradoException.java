package ucentral.edu.sadm.mascotas.dominio.exception;

import ucentral.edu.sadm.common.infraestructure.BusinessException;

public class DadorNoEncontradoException extends BusinessException {

  public DadorNoEncontradoException() {
    super(404, "El usuario dador no existe");
  }
}
