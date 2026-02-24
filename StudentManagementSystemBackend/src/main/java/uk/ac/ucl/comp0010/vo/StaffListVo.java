package uk.ac.ucl.comp0010.vo;

import lombok.Data;

/**
 * Value Object for showing information of Staffs.
 *
 * @author Jack Pan
 * @since 2024-11-03
 */
@Data
public class StaffListVo {

  /**
   * ID.
   */
  private Integer id;

  /**
   * First name.
   */

  private String firstName;

  /**
   * Last name Not Null.
   */
  private String lastName;

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
}
