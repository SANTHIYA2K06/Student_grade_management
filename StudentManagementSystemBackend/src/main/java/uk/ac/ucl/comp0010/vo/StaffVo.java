package uk.ac.ucl.comp0010.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.ac.ucl.comp0010.entity.Staff;

/**
 * Staff Value Object. Used to transfer staff details without exposing sensitive information.
 *
 * @author Wesley
 * @since 2024-11-14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffVo {

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
   */
  private String username;

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
   * Construct Staff Value Object using staff entity.
   *
   * @param staff Staff Entity
   */
  public StaffVo(Staff staff) {
    this.id = staff.getId();
    this.firstName = staff.getFirstName();
    this.lastName = staff.getLastName();
    this.username = staff.getUsername();
    this.email = staff.getEmail();
    this.title = staff.getTitle();
    this.department = staff.getDepartment();
  }
}
