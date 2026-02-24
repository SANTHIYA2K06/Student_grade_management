package uk.ac.ucl.comp0010.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import uk.ac.ucl.comp0010.dto.CreateRegistrationDto;

/**
 * <p>
 * Model for registration of student grades.
 * No same group of studentId and recordId
 * </p>
 *
 * @author Jack Pan
 * @since 2024-10-18
 */
@Entity
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Registration implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  /**
   * Student id.
   *
   * <p>
   * Not Null
   * </p>
   */

  private Integer studentId;

  /**
   * Record id.
   * <p>
   *   Not Null
   * </p>
   */
  private Long recordId;

  /**
   * Score.
   */

  private Integer score;

  /**
   * Registration Time.
   *
   * <p>
   * Not Null
   * </p>
   */

  private LocalDateTime registrationTime;

  /**
   * Create registration entity using createRegistrationDto.
   *
   * @param createRegistrationDto registration details
   */

  public Registration(CreateRegistrationDto createRegistrationDto) {
    this.studentId = createRegistrationDto.getStudentId();
    this.recordId = createRegistrationDto.getRecordId();
    this.score = createRegistrationDto.getScore();
    this.registrationTime = LocalDateTime.now();
  }


}
