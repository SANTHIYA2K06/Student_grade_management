package uk.ac.ucl.comp0010.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data object used to create Registration record.
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateRegistrationDto {

  /**
   * Student id.
   */

  private Integer studentId;

  /**
   * Record id.
   */

  private Long recordId;

  /**
   * Score.
   */

  private Integer score;

}
