package uk.ac.ucl.comp0010.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 * Data Object for getting details of record.
 *
 * @author Jack Pan
 * @since 2024-11-28
 */

@Data
public class RecordDetailVo {

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
   * Exam Date.
   */

  private LocalDate date;

  /**
   * Average Score.
   */

  private BigDecimal averageScore;

  /**
   * Number of Candidates.
   */

  private Integer numberOfCandidates;

  /**
   * Pass Rate.
   */

  private BigDecimal passRate;

}
