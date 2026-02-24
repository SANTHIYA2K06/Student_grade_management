package uk.ac.ucl.comp0010.exceptionhandler;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import uk.ac.ucl.comp0010.exception.CustomException;
import uk.ac.ucl.comp0010.exception.ServerException;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.response.ResultCode;

/**
 * Exception Handler for the Application.
 *
 * <p>
 * This class handles different types of exceptions thrown within the application and returns
 * appropriate responses with status codes and messages It is annotated with @ControllerAdvice,
 * meaning it is a global exception handler that applies to all controllers in the application.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-10-17
 */

@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles the NoResourceFoundException.
   *
   * <p>
   * This method handles cases where a requested resource is not found. It returns a 404 status code
   * along with a message indicating that the resource was not found.
   * </p>
   *
   * @return a ResponseEntity containing a Result object with a 404 status code and a "Not Found"
   *         message
   * @since 2024-10-17
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<Result<String>> handleNoResourceFoundException() {
    return ResponseEntity.status(ResultCode.NOT_FOUND.getCode())
        .body(new Result<>(ResultCode.NOT_FOUND.getCode(), ResultCode.NOT_FOUND.getMsg()));
  }

  /**
   * Handles CustomException.
   *
   * <p>
   * This method handles exceptions specific to the business logic, defined as CustomException. It
   * extracts the error code and message from the exception and returns the appropriate status
   * code.
   * </p>
   *
   * @param customException an instance of CustomException thrown within the application
   * @return a ResponseEntity containing a Result object with the appropriate status code and custom
   *         error message
   * @since 2024-10-17
   */
  @ExceptionHandler(CustomException.class)
  public ResponseEntity<Result<String>> handleCustomException(CustomException customException) {
    return ResponseEntity.status(customException.getCode().getCode())
        .body(new Result<>(customException.getCode().getCode(), customException.getMessage()));
  }

  /**
   * Handles ServerException.
   *
   * <p>
   * This method handles errors specific to the business logic, defined as ServerException. It
   * extracts the error code and message from the exception and returns the appropriate status
   * code.
   * </p>
   *
   * @param serverException an instance of CustomException thrown within the application
   * @return a ResponseEntity containing a Result object with the appropriate status code and custom
   *         error message
   * @since 2024-10-28
   */
  @ExceptionHandler(ServerException.class)
  public ResponseEntity<Result<String>> handleServerException(ServerException serverException) {
    return ResponseEntity.status(serverException.getCode().getCode())
        .body(new Result<>(serverException.getCode().getCode(), serverException.getMessage()));
  }


  /**
   * Handles HttpRequestMethodNotSupportedException.
   *
   * <p>
   * This method handles cases where the HTTP method used in a request is not allowed for the
   * particular resource. It returns a 405 status code and a message indicating that the method is
   * not allowed.
   * </p>
   *
   * @return a ResponseEntity containing a Result object with a 405 status code and a "Method Not
   *         Allowed" message
   * @since 2024-10-17
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<Result<String>> handleHttpRequestMethodNotSupportedException() {
    return ResponseEntity.status(ResultCode.Method_Not_Allowed.getCode())
        .body(new Result<>(ResultCode.Method_Not_Allowed.getCode(),
            ResultCode.Method_Not_Allowed.getMsg()));
  }

  /**
   * Handles HttpMessageNotReadableException.
   *
   * <p>
   * This method handles cases where the message is not acceptable for the particular resource. It
   * returns a 400 status code and a message indicating that the message is not acceptable
   * </p>
   *
   * @return a ResponseEntity containing a Result object with a 400 status code and a "Bad Request"
   *         message
   * @since 2024-10-17
   */

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Result<String>> handleHttpMessageNotReadableException() {
    return ResponseEntity.status(ResultCode.BAD_REQUEST.getCode())
        .body(new Result<>(ResultCode.BAD_REQUEST.getCode(),
            ResultCode.BAD_REQUEST.getMsg()));
  }

  /**
   * Handles MissingServletRequestParameterException.
   *
   * <p>
   * This method handles cases where the request is missing some important parameters. It returns a
   * 400 status code and a message indicating that the request is not acceptable
   * </p>
   *
   * @return a ResponseEntity containing a Result object with a 400 status code and a "Bad Request"
   *         message
   * @since 2024-10-23
   */

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<Result<String>> handleMissingServletRequestParameterException() {
    return ResponseEntity.status(ResultCode.BAD_REQUEST.getCode())
        .body(new Result<>(ResultCode.BAD_REQUEST.getCode(),
            ResultCode.BAD_REQUEST.getMsg()));
  }

  /**
   * Handles MissingServletRequestPartException.
   *
   * <p>
   * This method handles cases where the request is missing some important parameters. It returns a
   * 400 status code and a message indicating that the request is not acceptable
   * </p>
   *
   * @return a ResponseEntity containing a Result object with a 400 status code and a "Bad Request"
   *         message
   * @since 2024-10-28
   */

  @ExceptionHandler(MissingServletRequestPartException.class)
  public ResponseEntity<Result<String>> handleMissingServletRequestPartException() {
    return ResponseEntity.status(ResultCode.BAD_REQUEST.getCode())
        .body(new Result<>(ResultCode.BAD_REQUEST.getCode(),
            ResultCode.BAD_REQUEST.getMsg()));
  }


}
