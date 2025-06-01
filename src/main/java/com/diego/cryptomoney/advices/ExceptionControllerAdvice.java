package com.diego.cryptomoney.advices;

import com.diego.cryptomoney.exceptions.PriceNotFoundException;
import com.diego.cryptomoney.exceptions.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionControllerAdvice {
  @ExceptionHandler(PriceNotFoundException.class)
  public ResponseEntity<SimpleProblem> handleRuntimeException(PriceNotFoundException ex) {
    log.error("An error occurred: {}", ex.getMessage(), ex);
    return buildProblemResponseEntity(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<SimpleProblem> handleRuntimeException(UserAlreadyExistsException ex) {
    log.error("An error occurred: {}", ex.getMessage(), ex);
    return buildProblemResponseEntity(ex.getMessage(), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<SimpleProblem> handleRuntimeException(RuntimeException ex) {
    log.error("An error occurred: {}", ex.getMessage(), ex);
    return buildProblemResponseEntity(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<SimpleProblem> buildProblemResponseEntity(
      String message, HttpStatus status) {
    return new ResponseEntity<>(
        SimpleProblem.builder().status(status.value()).message(message).build(), status);
  }
}
