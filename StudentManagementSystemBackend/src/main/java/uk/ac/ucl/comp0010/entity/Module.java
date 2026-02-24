package uk.ac.ucl.comp0010.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.ac.ucl.comp0010.dto.CreateModuleDto;

/**
 * <p>
 * Module Model.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-10-18
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Module implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * Module Code Not Null Unique.
   */

  @Id
  @TableId("code")
  private String code;

  /**
   * Module Name.
   */

  private String name;

  /**
   * Is the module mandatory non-condonable.
   *
   * <p>
   * Default False
   * </p>
   */

  private Boolean mnc;

  /**
   * Credits.
   *
   * <p>
   * Default 0
   * </p>
   */
  private Integer credits;

  /**
   * Teaching Staff id.
   *
   * <p>
   * Not Null
   * </p>
   */

  private Integer staffId;

  /**
   * Constructor of Module using CreateModuleDto.
   *
   * @param createModuleDto Data Object for creating Module
   */

  public Module(CreateModuleDto createModuleDto) {
    this.code = createModuleDto.getCode();
    this.name = createModuleDto.getName();
    this.mnc = createModuleDto.getMnc();
    this.credits = createModuleDto.getCredits();
    this.staffId = createModuleDto.getStaffId();
  }


}
