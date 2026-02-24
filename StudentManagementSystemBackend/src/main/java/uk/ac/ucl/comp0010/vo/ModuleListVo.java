package uk.ac.ucl.comp0010.vo;

import lombok.Data;

/**
 * Module list Value Object.
 *
 * @author Jack Pan
 * @since 2024-11-10
 */

@Data
public class ModuleListVo {

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
