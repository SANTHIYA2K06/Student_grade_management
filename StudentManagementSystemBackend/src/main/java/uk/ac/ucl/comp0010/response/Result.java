package uk.ac.ucl.comp0010.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Response class for wrapping API responses.
 *
 * @param <T> Generic type of the data field.
 * @author Jack Pan
 * @since 2024-10-17
 */
@Data
@Accessors(chain = true)
public class Result<T> implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * Status Code.
   */
  private int code;

  /**
   * Response message.
   */
  private String message;

  /**
   * Data.
   */
  private T data;

  /**
   * Constructor for custom status code and message.
   *
   * @param code    Status code
   * @param message Custom message
   */
  public Result(int code, String message) {
    this.code = code;
    this.message = message;
  }

  /**
   * Constructor for custom status code, message, and data.
   *
   * @param code    Status code
   * @param message Custom message
   * @param data    Custom data
   */

  @JsonCreator
  public Result(@JsonProperty("code") int code,
      @JsonProperty("message") String message,
      @JsonProperty("data") T data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  /**
   * Success without data.
   *
   * @param <T> General Type for Result
   * @return General Success Object without data returned
   */
  public static <T> Result<T> success() {
    return new Result<>(ResultCode.SUCCESS.getCode(), "success", null);
  }

  /**
   * Success with data.
   *
   * @param data Data returned
   * @param <T>  General Type for Result
   * @return General Success Object with data returned
   */
  public static <T> Result<T> success(T data) {
    return new Result<>(ResultCode.SUCCESS.getCode(), "success", data);
  }

  /**
   * Success with customized message.
   *
   * @param message Custom message
   * @param <T>     General Type for Result
   * @return General Success Object with a custom message and no data
   */
  public static <T> Result<T> success(String message) {
    return new Result<>(ResultCode.SUCCESS.getCode(), message, null);
  }

  /**
   * Success with customized message and data.
   *
   * @param message Custom message
   * @param data    Custom data
   * @param <T>     General Type for Result
   * @return General Success Object with a custom message and data returned
   */
  public static <T> Result<T> success(String message, T data) {
    return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
  }

  /**
   * Error with a custom message and no data.
   *
   * @param <T>     General Type for Result
   * @param message Custom message
   * @return General Error Object with a custom message
   */
  public static <T> Result<T> error(String message) {
    return new Result<>(ResultCode.BAD_REQUEST.getCode(), message, null);
  }
}

