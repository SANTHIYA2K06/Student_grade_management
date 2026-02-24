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

/**
 * <p>
 * Admin Table.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-10-22
 */
@Entity
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Admin implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * id.
   */
  @Id
  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

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

}