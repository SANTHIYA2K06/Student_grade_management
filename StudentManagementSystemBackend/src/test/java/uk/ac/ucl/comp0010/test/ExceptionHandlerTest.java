package uk.ac.ucl.comp0010.test;

import org.junit.jupiter.api.Test;
import uk.ac.ucl.comp0010.exception.CustomException;
import uk.ac.ucl.comp0010.exception.ServerException;
import uk.ac.ucl.comp0010.exceptionhandler.GlobalExceptionHandler;

/**
 * Test handle exceptions.
 *
 * @author Jack Pan
 * @since 2024-12-03
 */
public class ExceptionHandlerTest {

  @Test
  public void testCustomExceptionThrown() {
    GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
    CustomException customException = new CustomException("This is a custom exception");
    globalExceptionHandler.handleCustomException(customException);
  }

  @Test
  public void testNoResourceFoundExceptionThrown() {
    GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
    globalExceptionHandler.handleNoResourceFoundException();
  }

  @Test
  public void testHttpMessageNotReadableExceptionThrown() {
    GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
    globalExceptionHandler.handleHttpMessageNotReadableException();
  }

  @Test
  public void testHttpRequestMethodNotSupportedExceptionThrown() {
    GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
    globalExceptionHandler.handleHttpRequestMethodNotSupportedException();
  }

  @Test
  public void testMissingServletRequestPartExceptionThrown() {
    GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
    globalExceptionHandler.handleMissingServletRequestPartException();
  }

  @Test
  public void testServerExceptionThrown() {
    GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
    ServerException serverException = new ServerException("This is a server exception");
    globalExceptionHandler.handleServerException(serverException);
  }

  @Test
  public void testMissingServletRequestParameterExceptionThrown() {
    GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
    globalExceptionHandler.handleMissingServletRequestParameterException();
  }
}
