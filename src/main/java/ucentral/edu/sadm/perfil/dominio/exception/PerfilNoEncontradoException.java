package ucentral.edu.sadm.perfil.dominio.exception;

import ucentral.edu.sadm.common.infraestructure.BusinessException;

public class PerfilNoEncontradoException extends BusinessException {

  public PerfilNoEncontradoException() {
    super(404, "No se encontró tu perfil");
  }

  public PerfilNoEncontradoException(String message) {
    super(404, message);
  }
}
