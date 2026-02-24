package uk.ac.ucl.comp0010.exception;

import uk.ac.ucl.comp0010.response.ResultCode;

/**
 * Unauthorized Exception.
 *
 * <p>
 * Call when the resource is accessed with no authentication.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-11-13
 */

public class UnauthorizedException extends CustomException {

  public UnauthorizedException(String message) {
    super(ResultCode.UNAUTHORIZED, message);
  }
}
