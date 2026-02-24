package uk.ac.ucl.comp0010.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create Staff Data object.
 *
 * @author Jack Pan
 * @since 2024-11-03
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateStaffDto {

  /**
   * First name.
   */

  private String firstName;

  /**
   * Last name Not Null.
   */
  private String lastName;

  /**
   * username Not Null.
   */
  private String username;

  /**
   * password Not Null.
   */
  private String password;

  /**
   * email.
   */
  private String email;

  /**
   * title.
   */
  private String title;

  /**
   * department.
   */
  private String department;
}
