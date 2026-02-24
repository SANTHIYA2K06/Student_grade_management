package uk.ac.ucl.comp0010.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * Value Object for students to see their own grades.
 */

@Data
public class StudentRegistrationListVo {

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
   * Credits.
   */
  private Integer credits;

  /**
   * Exam Record Date.
   */

  private LocalDate examDate;

  /**
   * Score.
   */
  private Integer score;

  /**
   * Registration Time.
   */
  private LocalDateTime registrationTime;
}
