package ucentral.edu.sadm.perfil.dominio.exception;

import ucentral.edu.sadm.common.infraestructure.BusinessException;

public class FotoPerfilInvalidaException extends BusinessException {

  public FotoPerfilInvalidaException(String message) {
    super(400, message);
  }
}
