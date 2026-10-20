package ucentral.edu.sadm.common.infraestructure;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.singletonList;

@Data
public class BusinessException extends RuntimeException {

  private final int code;
  private final List<String> messages;
  private final String errorCode;
  private final String detail;

  public BusinessException(int code, String message) {
    super(message);
    this.code = code;
    this.messages = new LinkedList<>(singletonList(message));
    this.errorCode = null;
    this.detail = null;
  }

  public BusinessException(int code, List<String> messages) {
    super(String.join(", ", messages));
    this.code = code;
    this.messages = messages;
    this.errorCode = null;
    this.detail = null;
  }

  public BusinessException(int code, String errorCode, String message, String detail) {
    super(message);
    this.code = code;
    this.messages = new LinkedList<>(singletonList(message));
    this.errorCode = errorCode;
    this.detail = detail;
  }
}