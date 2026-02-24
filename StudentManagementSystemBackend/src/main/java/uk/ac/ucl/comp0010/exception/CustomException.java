package uk.ac.ucl.comp0010.exception;

import lombok.Getter;
import uk.ac.ucl.comp0010.response.ResultCode;

/**
 * Custom Exception throw during processing data.
 *
 * @author Jack Pan
 * @since 2024-10-17
 */

@Getter
public class CustomException extends RuntimeException {

  private final ResultCode code;

  public CustomException(ResultCode code, String message) {
    super(message);
    this.code = code;
  }

  public CustomException(String message) {
    super(message);
    this.code = ResultCode.BAD_REQUEST;
  }

}