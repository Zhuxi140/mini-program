package com.zhuxi.Exception;

public class transactionalException extends LocatedException {

  private final int code;
  public transactionalException(String message) {
    super(message);
    this.code = 500;
  }

  public int getCode() {
    return code;
  }
}
