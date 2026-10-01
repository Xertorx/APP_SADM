package ucentral.edu.sadm.adoptantes.dominio.exception;

import ucentral.edu.sadm.common.infraestructure.BusinessException;

public class EmailAlreadyExistsException extends BusinessException {

  public EmailAlreadyExistsException() {
    super(409, "El correo ingresado ya existe");
  }
}