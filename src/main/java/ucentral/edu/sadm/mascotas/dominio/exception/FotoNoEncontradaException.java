package ucentral.edu.sadm.mascotas.dominio.exception;

import ucentral.edu.sadm.common.infraestructure.BusinessException;

public class FotoNoEncontradaException extends BusinessException {

  public FotoNoEncontradaException() {
    super(404, "La foto no existe");
  }
}
