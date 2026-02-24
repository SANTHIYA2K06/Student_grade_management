package uk.ac.ucl.comp0010.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ucl.comp0010.aspect.LoginAspect;
import uk.ac.ucl.comp0010.dto.CreateStudentDto;
import uk.ac.ucl.comp0010.dto.EditStudentDto;
import uk.ac.ucl.comp0010.dto.LoginDto;
import uk.ac.ucl.comp0010.dto.ResetPasswordDto;
import uk.ac.ucl.comp0010.entity.Student;
import uk.ac.ucl.comp0010.enums.AccountType;
import uk.ac.ucl.comp0010.enums.JwtType;
import uk.ac.ucl.comp0010.exception.CustomException;
import uk.ac.ucl.comp0010.mapper.RegistrationMapper;
import uk.ac.ucl.comp0010.mapper.StudentMapper;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.service.StudentService;
import uk.ac.ucl.comp0010.utils.CsvUtils;
import uk.ac.ucl.comp0010.utils.JwtUtils;
import uk.ac.ucl.comp0010.utils.PasswordEncoderUtils;
import uk.ac.ucl.comp0010.vo.LoginVo;
import uk.ac.ucl.comp0010.vo.StudentListVo;
import uk.ac.ucl.comp0010.vo.StudentRegistrationListVo;
import uk.ac.ucl.comp0010.vo.StudentVo;

/**
 * <p>
 * Student Implementation for Student Service.
 * </p>
 *
 * @author Jack Pan,Wesley Xu
 * @since 2024-11-14
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements
    StudentService {

  @Resource
  private StudentMapper studentMapper;

  @Resource
  private RegistrationMapper registrationMapper;

  /**
   * Student login.
   *
   * @param loginDto login data object
   * @return accessToken and refreshToken
   */
  @Override
  public Result<LoginVo> login(LoginDto loginDto) {
    Student student = this.getOne(
        new QueryWrapper<Student>().eq("username", loginDto.getUsername()));
    if (student == null) {
      throw new CustomException("No such username");
    }
    if (!PasswordEncoderUtils.checkPassword(loginDto.getPassword(), student.getPassword())) {
      throw new CustomException("Username or Password Incorrect");
    }
    String accessToken = JwtUtils.generateJwtToken(student.getId(), AccountType.student,
        JwtType.access_token);
    String refreshToken = JwtUtils.generateJwtToken(student.getId(), AccountType.student,
        JwtType.refresh_token);
    return Result.success(new LoginVo(accessToken, refreshToken));
  }

  /**
   * add student.
   *
   * @param createStudentDto student details
   * @return success
   */
  @Override
  public Result<Object> addStudent(CreateStudentDto createStudentDto) {
    // Integrity Check
    if (createStudentDto.getUsername() == null || createStudentDto.getUsername().isBlank()
        || createStudentDto.getPassword() == null || createStudentDto.getPassword().isBlank()) {
      throw new CustomException("Username or password can't be null");
    }
    // Unique Check
    Student student = this.getOne(
        new QueryWrapper<Student>().eq("username", createStudentDto.getUsername()));
    if (student != null) {
      throw new CustomException("Username already exist!");
    }
    student = new Student(createStudentDto);
    this.save(student);
    return Result.success();
  }

  /**
   * list students.
   *
   * @param current current page
   * @param size    page size
   * @return data
   */
  @Override
  public Result<IPage<StudentListVo>> listStudents(Integer current, Integer size, Integer id,
      String fullName, String username, String programOfStudy) {
    QueryWrapper<StudentListVo> wrapper = new QueryWrapper<>();
    if (id != null) {
      wrapper.eq("id", id);
    }
    if (fullName != null && !fullName.isBlank()) {
      wrapper.apply("CONCAT(first_name, ' ', last_name) LIKE {0}",
          "%" + fullName + "%");
    }
    if (username != null && !username.isBlank()) {
      wrapper.like("username", username);
    }
    if (programOfStudy != null && !programOfStudy.isBlank()) {
      wrapper.like("program_of_study", programOfStudy);
    }

    IPage<StudentListVo> ipage = new Page<>(current, size);
    return Result.success(studentMapper.listStudents(ipage, wrapper));
  }

  /**
   * import or update students using csv file.
   *
   * @param file csv file
   * @return success
   */
  @Transactional
  @Override
  public Result<Object> importStudent(MultipartFile file) {
    List<CreateStudentDto> records = CsvUtils.readCsv(file, CreateStudentDto.class);
    Set<String> existingUsernames = this.list().stream()
        .map(Student::getUsername)
        .collect(Collectors.toSet());
    List<Student> savingStudentList = new ArrayList<>();
    List<Student> updatingStudentList = new ArrayList<>();
    for (CreateStudentDto record : records) {
      // Integrity Check
      if (record.getUsername() == null || record.getUsername().isBlank()
          || record.getPassword() == null || record.getPassword().isBlank()) {
        throw new CustomException("Username or password can't be null");
      }
      if (!existingUsernames.contains(record.getUsername())) {
        savingStudentList.add(new Student(record));
      } else {
        updatingStudentList.add(new Student(record));
      }
    }
    this.saveBatch(savingStudentList);
    this.updateBatchById(updatingStudentList);
    return Result.success();
  }

  /**
   * Edit student information.
   *
   * @param editStudentDto Data object containing fields to be updated
   * @param id             The ID of the student to be edited
   * @return Result indicating the success of the operation
   * @throws CustomException if the student does not exist or if there are validation errors
   * @author Wesley Xu
   */
  @Override
  public Result<Object> edit(EditStudentDto editStudentDto, Integer id) {
    Student student = this.getById(id);
    if (student == null) {
      throw new CustomException("Student not exist!");
    }
    // Integrity Check
    if (editStudentDto.getUsername() != null && editStudentDto.getUsername().isBlank()) {
      throw new CustomException("Username can't be empty");
    }
    if (editStudentDto.getPassword() != null && editStudentDto.getPassword().isBlank()) {
      throw new CustomException("Password can't be empty");
    }
    // Unique Check
    if (editStudentDto.getUsername() != null
        && !editStudentDto.getUsername().equals(student.getUsername())) {
      Student studentForCheck = this.getOne(
          new QueryWrapper<Student>().eq("username", editStudentDto.getUsername())
      );
      if (studentForCheck != null) {
        throw new CustomException("Username already exist!");
      }
    }
    // Update student properties
    student.setUsername(editStudentDto.getUsername());
    if (editStudentDto.getPassword() != null) {
      student.setPassword(PasswordEncoderUtils.encode(editStudentDto.getPassword()));
    }
    student.setFirstName(editStudentDto.getFirstName());
    student.setLastName(editStudentDto.getLastName());
    student.setEmail(editStudentDto.getEmail());
    if (editStudentDto.getBirthDate() != null) {
      student.setBirthDate(editStudentDto.getBirthDate());
    }
    student.setProgramOfStudy(editStudentDto.getProgramOfStudy());
    student.setGraduationYear(editStudentDto.getGraduationYear());
    student.setDepartment(editStudentDto.getDepartment());

    this.updateById(student);
    return Result.success();
  }

  /**
   * Delete a student by ID.
   *
   * @param id The ID of the student to be deleted
   * @return Result indicating the success of the operation
   * @throws CustomException if the student does not exist
   * @author Wesley Xu
   */
  @Override
  public Result<Object> delete(Integer id) {
    Student student = this.getById(id);
    if (student == null) {
      throw new CustomException("Student not exist!");
    }
    this.removeById(id);
    return Result.success();
  }

  /**
   * Resets the password for a student account.
   * Validates the old password before updating to the new password.
   *
   * @param accountId         the ID of the student account whose password is to be reset
   * @param resetPasswordDto  the data transfer object containing the old and new passwords
   * @throws CustomException if:
   *                         <ul>
   *                           <li>The student account with the given ID does not exist</li>
   *                           <li>The old password provided does not match the stored password</li>
   *                         </ul>
   */
  @Override
  public void resetPassword(Integer accountId, ResetPasswordDto resetPasswordDto) {
    Student student = this.getById(accountId);
    if (student == null) {
      throw new CustomException("Student not found");
    }
    if (!PasswordEncoderUtils.checkPassword(resetPasswordDto.getOldPassword(),
        student.getPassword())) {
      throw new CustomException("Old password is incorrect");
    }
    student.setPassword(PasswordEncoderUtils.encode(resetPasswordDto.getNewPassword()));
    this.updateById(student);
  }

  /**
   * Students get their own details.
   *
   * @return Student Value Object
   */
  @Override
  public Result<StudentVo> detail() {
    Student student = this.getById(LoginAspect.threadLocal.get().getId());
    return Result.success(new StudentVo(student));
  }

  /**
   * Staff and Admin get student details.
   *
   * @param id id of the student
   * @return student detail
   */
  @Override
  public Result<StudentVo> getStudentDetail(Integer id) {
    Student student = this.getById(id);
    if (student == null) {
      throw new CustomException("Student not found");
    }

    // Convert Student entity to StudentVo, excluding password
    StudentVo studentVo = new StudentVo(student);
    return Result.success(studentVo);
  }

  /**
   * List student's own scores.
   *
   * @param current current Page
   * @param size    page size
   * @return data
   */
  @Override
  public Result<IPage<StudentRegistrationListVo>> listStudentRegistrations(Integer current,
      Integer size) {
    return Result.success(registrationMapper.listStudentRegistrations(new Page<>(current, size),
        LoginAspect.threadLocal.get().getId()));
  }

  /**
   * Retrieves a list of unique academic programs associated with students.
   * Ensures that duplicate program names are removed from the result.
   *
   * @return a Result object containing a list of unique program names
   */
  @Override
  public Result<List<String>> listPrograms() {
    List<String> programs = studentMapper.listPrograms();
    programs = new ArrayList<>(new HashSet<>(programs));
    return Result.success(programs);
  }

}