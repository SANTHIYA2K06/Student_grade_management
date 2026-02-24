package uk.ac.ucl.comp0010.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Object for editing record.
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EditRecordDto {

  /**
   * module Code.
   */
  private String moduleCode;

  /**
   * Exam date.
   */
  private LocalDate date;

}
