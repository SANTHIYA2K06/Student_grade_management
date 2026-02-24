package uk.ac.ucl.comp0010.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create Student Data object.
 *
 * @author Jack Pan
 * @since 2024-10-24
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateStudentDto {

  /**
   * First name.
   */

  private String firstName;

  /**
   * Last name.
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
   * Date of Birth.
   */
  private LocalDate birthDate;

  /**
   * program of study.
   */
  private String programOfStudy;

  /**
   * graduating year.
   */
  private Integer graduationYear;

  /**
   * Department.
   */
  private String department;

}
