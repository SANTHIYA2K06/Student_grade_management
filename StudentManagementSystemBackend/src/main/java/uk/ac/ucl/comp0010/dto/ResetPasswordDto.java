package uk.ac.ucl.comp0010.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Reset Password data object.
 *
 * @author Wesley Xu
 * @since 2024-11-12
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDto {

  private String oldPassword;
  private String newPassword;
}
