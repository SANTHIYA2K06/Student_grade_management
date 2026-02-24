package uk.ac.ucl.comp0010.vo;

import lombok.Data;

/**
 * Data Object for getting details of a module.
 *
 * @author Jack Pan
 * @since 2024-11-28
 */
@Data
public class ModuleDetailVo {

  /**
   * Module Code.
   */

  private String code;

  /**
   * Module Name.
   */

  private String name;

  /**
   * Is the module mandatory non-condonable.
   */

  private Boolean mnc;

  /**
   * Credits.
   */
  private Integer credits;

  /**
   * Teaching Staff First Name.
   */

  private String staffFirstName;

  /**
   * Teaching Staff Last Name.
   */

  private String staffLastName;

}
