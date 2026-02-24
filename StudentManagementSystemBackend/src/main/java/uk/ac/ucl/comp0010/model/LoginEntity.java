package uk.ac.ucl.comp0010.model;

import lombok.Data;
import uk.ac.ucl.comp0010.enums.AccountType;

/**
 * Login Entity for logged-in users.
 *
 * @author Jack Pan, Wesley Xu
 * @since 2024-11-13
 */
@Data
public class LoginEntity {

  private Integer id;
  private AccountType accountType;

}
