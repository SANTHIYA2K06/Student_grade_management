package uk.ac.ucl.comp0010.dto;

import lombok.Data;

/**
 * Data object for login.
 *
 * @author Jack Pan
 * @since 2024-10-17
 */

@Data
public class LoginDto {

  private String username;
  private String password;

}
