package uk.ac.ucl.comp0010.response;

import lombok.Getter;

/**
 * Result Code for response.
 *
 * @author Jack Pan
 * @since 2024-10-17
 */
@Getter
public enum ResultCode {

  SUCCESS(200, "Success"),
  BAD_REQUEST(400, "Bad Request"),
  UNAUTHORIZED(401, "Unauthorized"),
  FORBIDDEN(403, "Forbidden"),
  NOT_FOUND(404, "Not Found"),
  Method_Not_Allowed(405, "Method Not Allowed"),
  ERROR(500, "Internal Service Error");

  /**
   * Status Code.
   */
  private final int code;

  /**
   * Message.
   */
  private final String msg;

  ResultCode(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

}

