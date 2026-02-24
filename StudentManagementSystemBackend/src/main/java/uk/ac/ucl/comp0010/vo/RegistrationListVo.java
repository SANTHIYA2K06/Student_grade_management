package uk.ac.ucl.comp0010.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * List Score Registration Value Object.
 *
 * @author Jack Pan
 * @since 2024-11-27
 */

@Data
public class RegistrationListVo {

  /**
   * id.
   */
  private Long id;

  /**
   * Module Code.
   */

  private String moduleCode;

  /**
   * Module Name.
   */

  private String moduleName;

  /**
   * Exam Record Date.
   */

  private LocalDate examDate;

  /**
   * Score.
   */
  private Integer score;

  /**
   * Student Id.
   */
  private Integer studentId;

  /**
   * Student First Name.
   */
  private String studentFirstName;

  /**
   * Student Last Name.
   */
  private String studentLastName;

  /**
   * Registration Time.
   */
  private LocalDateTime registrationTime;

}
