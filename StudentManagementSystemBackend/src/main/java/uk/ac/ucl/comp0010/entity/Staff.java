package uk.ac.ucl.comp0010.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import uk.ac.ucl.comp0010.dto.CreateStaffDto;

/**
 * <p>
 * Staff Table.
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
public class Staff implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * id.
   */
  @Id
  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

  /**
   * First name.
   */

  private String firstName;

  /**
   * Last name.
   */
  private String lastName;

  /**
   * username.
   *
   * <p>
   * Unique Not Null
   * </p>
   */
  private String username;

  /**
   * password.
   *
   * <p>
   * Not Null
   * </p>
   */
  private String password;

  /**
   * email.
   */
  private String email;

  /**
   * title.
   */
  private String title;

  /**
   * department.
   */
  private String department;

  /**
   * Create Staff using create staff data object.
   *
   * @param createStaffDto create staff data object
   */

  public Staff(CreateStaffDto createStaffDto) {
    this.firstName = createStaffDto.getFirstName();
    this.lastName = createStaffDto.getLastName();
    this.username = createStaffDto.getUsername();
    this.password = createStaffDto.getPassword();
    this.email = createStaffDto.getEmail();
    this.title = createStaffDto.getTitle();
    this.department = createStaffDto.getDepartment();
  }


}
