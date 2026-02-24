package uk.ac.ucl.comp0010.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ucl.comp0010.annotation.LoginRequired;
import uk.ac.ucl.comp0010.dto.CreateStudentDto;
import uk.ac.ucl.comp0010.dto.EditStudentDto;
import uk.ac.ucl.comp0010.dto.LoginDto;
import uk.ac.ucl.comp0010.enums.AccountType;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.service.StudentService;
import uk.ac.ucl.comp0010.vo.LoginVo;
import uk.ac.ucl.comp0010.vo.StudentListVo;
import uk.ac.ucl.comp0010.vo.StudentRegistrationListVo;
import uk.ac.ucl.comp0010.vo.StudentVo;

/**
 * <p>
 * Student related API controller.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-10-17
 */
@RestController
@RequestMapping("/api/student")
public class StudentController {

  @Resource
  private StudentService studentService;

  /**
   * Student Login.
   *
   * @param loginDto login data object.
   * @return accessToken and refreshToken
   */
  @PostMapping("/login")
  public Result<LoginVo> login(@RequestBody LoginDto loginDto) {
    return studentService.login(loginDto);
  }

  /**
   * Add student.
   *
   * @param createStudentDto student details.
   * @return success
   */
  @LoginRequired(accountTypes = {AccountType.admin})
  @PostMapping("/add-student")
  public Result<Object> addStudent(@RequestBody CreateStudentDto createStudentDto) {
    return studentService.addStudent(createStudentDto);
  }

  /**
   * Retrieves the personal information of the logged-in student.
   * This endpoint is accessible only to authenticated users with the account type {@code student}.
   *
   * @return a Result object containing the personal information as a {@code StudentVo}
   */
  @LoginRequired(accountTypes = {AccountType.student})
  @GetMapping("/detail")
  public Result<StudentVo> detail() {
    return studentService.detail();
  }

  /**
   * Retrieves a paginated list of students based on optional filtering criteria.
   * This endpoint is accessible to authenticated users with the account types
   * {@code admin} or {@code staff}.
   *
   * @param current         the current page number
   * @param size            the number of records per page
   * @param id              (optional) the ID of the student to filter by
   * @param fullName        (optional) the full name of the student to filter by
   * @param username        (optional) the username of the student to filter by
   * @param programOfStudy  (optional) the program of study to filter by
   * @return a Result object containing a paginated list of {@code StudentListVo} objects
   */
  @LoginRequired(accountTypes = {AccountType.admin, AccountType.staff})
  @GetMapping("/list-students")
  public Result<IPage<StudentListVo>> listStudents(
      @RequestParam Integer current,
      @RequestParam Integer size,
      @RequestParam(required = false) Integer id,
      @RequestParam(required = false) String fullName,
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String programOfStudy) {
    return studentService.listStudents(current, size, id, fullName, username,
        programOfStudy);
  }

  /**
   * Imports students from a provided CSV file.
   * This endpoint is accessible only to authenticated users with the account type {@code admin}.
   *
   * @param file the CSV file containing the student data to import
   * @return a Result object indicating the success or failure of the import operation
   */
  @LoginRequired(accountTypes = {AccountType.admin})
  @PostMapping("/import-students")
  public Result<Object> importStudents(@RequestParam MultipartFile file) {
    return studentService.importStudent(file);
  }

  /**
   * Edits an existing student's details based on the provided information and ID.
   * This endpoint is accessible only to authenticated users with the account type {@code admin}.
   *
   * @param editStudentDto the data transfer object containing the updated details for the student
   * @param id             the ID of the student to edit
   * @return a Result object indicating the success or failure of the operation
   */
  @LoginRequired(accountTypes = {AccountType.admin})
  @PutMapping("/edit/{id}")
  public Result<Object> edit(@RequestBody EditStudentDto editStudentDto, @PathVariable Integer id) {
    return studentService.edit(editStudentDto, id);
  }

  /**
   * Deletes a specific student by their ID.
   * This endpoint is accessible only to authenticated users with the account type {@code admin}.
   *
   * @param id the ID of the student to delete
   * @return a Result object indicating the success or failure of the deletion
   */
  @LoginRequired(accountTypes = {AccountType.admin})
  @DeleteMapping("/delete/{id}")
  public Result<Object> delete(@PathVariable Integer id) {
    return studentService.delete(id);
  }

  /**
   * Retrieves detailed information about a specific student by their ID.
   * This endpoint is accessible to authenticated users with the account types
   * {@code admin} or {@code staff}.
   *
   * @param id the ID of the student to retrieve
   * @return a Result object containing detailed information about the student as a
   *        {@code StudentVo}
   */
  @LoginRequired(accountTypes = {AccountType.admin, AccountType.staff})
  @GetMapping("/{id}")
  public Result<StudentVo> getStudentDetails(@PathVariable Integer id) {
    return studentService.getStudentDetail(id);
  }

  /**
   * Retrieves a paginated list of the logged-in student's own registrations.
   * This endpoint is accessible only to authenticated users with the account type {@code student}.
   *
   * @param current the current page number
   * @param size    the number of records per page
   * @return a Result object containing a paginated list of
   *        {@code StudentRegistrationListVo} objects
   */
  @LoginRequired(accountTypes = {AccountType.student})
  @GetMapping("/get-registrations")
  public Result<IPage<StudentRegistrationListVo>> listStudentRegistrations(
      @RequestParam Integer current, @RequestParam Integer size) {
    return studentService.listStudentRegistrations(current, size);
  }

  /**
   * Retrieves a list of unique academic programs associated with students.
   * This endpoint is accessible to authenticated users with the account types
   * {@code admin} or {@code staff}.
   *
   * @return a Result object containing a list of program names
   */
  @LoginRequired(accountTypes = {AccountType.admin, AccountType.staff})
  @GetMapping("/list-programs")
  public Result<List<String>> listPrograms() {
    return studentService.listPrograms();
  }


}