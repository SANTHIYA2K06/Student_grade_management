package uk.ac.ucl.comp0010.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import uk.ac.ucl.comp0010.dto.CreateStudentDto;
import uk.ac.ucl.comp0010.utils.PasswordEncoderUtils;

/**
 * <p>
 * Student Table.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-1-24
 */
@Entity
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Student implements Serializable {

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
   * Constructs a new Student from CreateStudentDto.
   *
   * @param createStudentDto the DTO with student data
   */
  public Student(CreateStudentDto createStudentDto) {
    this.firstName = createStudentDto.getFirstName();
    this.lastName = createStudentDto.getLastName();
    this.username = createStudentDto.getUsername();
    this.password = PasswordEncoderUtils.encode(createStudentDto.getPassword());
    this.email = createStudentDto.getEmail();
    this.birthDate = createStudentDto.getBirthDate();
    this.programOfStudy = createStudentDto.getProgramOfStudy();
    this.graduationYear = createStudentDto.getGraduationYear();
    this.department = createStudentDto.getDepartment();
  }

}

