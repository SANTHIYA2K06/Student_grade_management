package uk.ac.ucl.comp0010.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data object used to create module.
 *
 * @author Jack Pan
 * @since 2024-11-10
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateModuleDto {

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
   * Teaching Staff id.
   */

  private Integer staffId;
}
