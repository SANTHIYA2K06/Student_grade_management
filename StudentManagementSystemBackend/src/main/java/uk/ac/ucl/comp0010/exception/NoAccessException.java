package uk.ac.ucl.comp0010.exception;

import uk.ac.ucl.comp0010.response.ResultCode;

/**
 * No Access Exception.
 *
 * <p>
 * Call when the resource is accessed with no access
 * </p>
 *
 * @author Jack Pan
 * @since 2024-11-13
 */

public class NoAccessException extends CustomException {

  public NoAccessException(String message) {
    super(ResultCode.FORBIDDEN, message);
  }
}