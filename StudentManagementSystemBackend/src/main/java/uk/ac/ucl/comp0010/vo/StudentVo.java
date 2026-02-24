package uk.ac.ucl.comp0010.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.ac.ucl.comp0010.entity.Student;

/**
 * Student Value Object.
 *
 * @author Jack Pan
 * @since 2024-11-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentVo {

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
   * Date of Birth.
   */
  private LocalDate birthDate;

  /**
   * program of study.
   */
  private String programOfStudy;

  /**
   * graduating year.
   */
  private Integer graduationYear;

  /**
   * Department.
   */
  private String department;

  /**
   * Construct Student Value Object using student entity.
   *
   * @param student Student Entity
   */

  public StudentVo(Student student) {
    this.id = student.getId();
    this.firstName = student.getFirstName();
    this.lastName = student.getLastName();
    this.username = student.getUsername();
    this.email = student.getEmail();
    this.birthDate = student.getBirthDate();
    this.programOfStudy = student.getProgramOfStudy();
    this.graduationYear = student.getGraduationYear();
    this.department = student.getDepartment();
  }

}
