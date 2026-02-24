package uk.ac.ucl.comp0010.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import uk.ac.ucl.comp0010.dto.CreateRecordDto;

/**
 * <p>
 * Model for records of exams.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-11-22
 */
@Entity
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Record implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  /**
   * module Code.
   * <p>
   * Not Null
   * </p>
   */
  private String moduleCode;

  /**
   * Exam date.
   *
   * <p>
   * Not Null
   * </p>
   */

  private LocalDate date;

  public Record(CreateRecordDto createRecordDto) {
    this.moduleCode = createRecordDto.getModuleCode();
    this.date = createRecordDto.getDate();
  }

}
