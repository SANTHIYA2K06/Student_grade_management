package uk.ac.ucl.comp0010.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data object for creating record.
 *
 * @author Jack Pan
 * @since 2024-11-25
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateRecordDto {

  /**
   * module Code.
   */
  private String moduleCode;

  /**
   * Exam date.
   */
  private LocalDate date;

}
