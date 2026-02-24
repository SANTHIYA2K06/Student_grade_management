package uk.ac.ucl.comp0010.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login Data to return.
 *
 * @author Jack Pan
 * @since 2024-10-17
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginVo {

  private String accessToken;

  private String refreshToken;

}
