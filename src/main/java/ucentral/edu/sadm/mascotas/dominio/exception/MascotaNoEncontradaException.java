package ucentral.edu.sadm.mascotas.dominio.exception;

import ucentral.edu.sadm.common.infraestructure.BusinessException;

public class MascotaNoEncontradaException extends BusinessException {

  public MascotaNoEncontradaException() {
    super(404, "La mascota no existe");
  }
}
