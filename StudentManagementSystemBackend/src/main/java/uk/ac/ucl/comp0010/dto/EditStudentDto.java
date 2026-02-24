package uk.ac.ucl.comp0010.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data object for editing student info.
 *
 * @author Wesley Xu
 * @since 2024-11-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditStudentDto {

  /**
   * First name.
   */
  private String firstName;

  /**
   * Last name.
   */
  private String lastName;

  /**
   * Username.
   */
  private String username;

  /**
   * Password.
   */
  private String password;

  /**
   * Email.
   */
  private String email;

  /**
   * Birthdate.
   */
  private LocalDate birthDate;

  /**
   * Program of study.
   */
  private String programOfStudy;

  /**
   * Graduation year.
   */
  private Integer graduationYear;

  /**
   * Department.
   */
  private String department;
}
