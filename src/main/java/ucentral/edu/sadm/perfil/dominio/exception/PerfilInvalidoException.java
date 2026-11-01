package ucentral.edu.sadm.perfil.dominio.exception;

import ucentral.edu.sadm.common.infraestructure.BusinessException;

/** Error 400 de reglas del perfil (bloque de contraseña, foto + quitarFoto…). */
public class PerfilInvalidoException extends BusinessException {

  public PerfilInvalidoException(String message) {
    super(400, message);
  }
}
