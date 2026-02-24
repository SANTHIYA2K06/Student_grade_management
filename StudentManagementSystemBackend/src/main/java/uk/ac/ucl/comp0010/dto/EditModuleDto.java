package uk.ac.ucl.comp0010.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data object used to edit module.
 *
 * @author Jack Pan
 * @since 2024-11-11
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditModuleDto {

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
