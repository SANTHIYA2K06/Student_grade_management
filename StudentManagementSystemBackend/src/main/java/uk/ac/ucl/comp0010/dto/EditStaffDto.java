package uk.ac.ucl.comp0010.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data object for editing staff info.
 *
 * @author Jack Pan
 * @since 2024-11-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditStaffDto {

  /**
   * First name.
   */

  private String firstName;

  /**
   * Last name Not Null.
   */
  private String lastName;

  /**
   * email.
   */
  private String email;

  /**
   * username.
   */
  private String username;

  /**
   * password.
   */
  private String password;

  /**
   * title.
   */
  private String title;

  /**
   * department.
   */
  private String department;

}
