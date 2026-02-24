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
import uk.ac.ucl.comp0010.dto.CreateStaffDto;
import uk.ac.ucl.comp0010.dto.EditStaffDto;
import uk.ac.ucl.comp0010.dto.LoginDto;
import uk.ac.ucl.comp0010.enums.AccountType;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.service.StaffService;
import uk.ac.ucl.comp0010.vo.LoginVo;
import uk.ac.ucl.comp0010.vo.StaffListVo;
import uk.ac.ucl.comp0010.vo.StaffVo;

/**
 * <p>
 * Staff Controller.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-10-18
 */
@RestController
@RequestMapping("/api/staff")
public class StaffController {

  @Resource
  private StaffService staffService;

  /**
   * Handles login requests for staff accounts.
   *
   * @param loginDto the data transfer object containing
   *                 the login credentials (username and password)
   * @return a Result object containing login details, including a token and account information
   */
  @PostMapping("/login")
  public Result<LoginVo> login(@RequestBody LoginDto loginDto) {
    return staffService.login(loginDto);
  }

  /**
   * Adds a new staff member based on the provided details.
   * This endpoint is accessible only to authenticated users with the account type {@code admin}.
   *
   * @param createStaffDto the data transfer object containing the details for the new staff member
   * @return a Result object indicating the success or failure of the addition
   */
  @LoginRequired(accountTypes = {AccountType.admin})
  @PostMapping("/add-staff")
  public Result<Object> addStaff(@RequestBody CreateStaffDto createStaffDto) {
    return staffService.addStaff(createStaffDto);
  }

  /**
   * Retrieves personal information of the logged-in staff member.
   * This endpoint is accessible only to authenticated users with the account type {@code staff}.
   *
   * @return a Result object containing personal information as a {@code StaffVo}
   */
  @LoginRequired(accountTypes = {AccountType.staff})
  @GetMapping("/detail")
  public Result<StaffVo> detail() {
    return staffService.detail();
  }

  /**
   * Retrieves a paginated list of staff members based
   * on optional filtering criteria.
   * This endpoint is accessible to authenticated users with
   * the account types {@code admin} or {@code staff}.
   *
   * @param current    the current page number
   * @param size       the number of records per page
   * @param id         (optional) the ID of the staff to filter by
   * @param fullName   (optional) the full name of the staff to filter by
   * @param department (optional) the department of the staff to filter by
   * @param title      (optional) the title of the staff to filter by
   * @return a Result object containing a paginated list of {@code StaffListVo} objects
   */
  @LoginRequired(accountTypes = {AccountType.staff, AccountType.admin})
  @GetMapping("/list-staffs")
  public Result<IPage<StaffListVo>> listStaffs(@RequestParam Integer current,
      @RequestParam Integer size,
      @RequestParam(required = false) Integer id,
      @RequestParam(required = false) String fullName,
      @RequestParam(required = false) String department,
      @RequestParam(required = false) String title) {
    return staffService.listStaffs(current, size, id, fullName, department, title);
  }

  /**
   * Imports staff members from a provided CSV file.
   * This endpoint is accessible only to authenticated users with the account type {@code admin}.
   *
   * @param file the CSV file containing the staff data to import
   * @return a Result object indicating the success or failure of the import operation
   */
  @LoginRequired(accountTypes = {AccountType.admin})
  @PostMapping("/import-staffs")
  public Result<Object> importStaffs(@RequestParam MultipartFile file) {
    return staffService.importStaff(file);
  }

  /**
   * Edits an existing staff member based on
   * the provided details and ID.
   * This endpoint is accessible only to authenticated users with
   * the account type {@code admin}.
   *
   * @param editStaffDto the data transfer object containing
   *                     the updated details for the staff member
   * @param id           the ID of the staff member to edit
   * @return a Result object indicating the success or failure of the operation
   */
  @LoginRequired(accountTypes = {AccountType.admin})
  @PutMapping("/edit/{id}")
  public Result<Object> edit(@RequestBody EditStaffDto editStaffDto,
      @PathVariable Integer id) {
    return staffService.edit(editStaffDto, id);
  }

  /**
   * Deletes a specific staff member by their ID.
   * This endpoint is accessible only to authenticated users with the account type {@code admin}.
   *
   * @param id the ID of the staff member to delete
   * @return a Result object indicating the success or failure of the deletion
   */
  @LoginRequired(accountTypes = {AccountType.admin})
  @DeleteMapping("/delete/{id}")
  public Result<Object> delete(@PathVariable Integer id) {
    return staffService.delete(id);
  }

  /**
   * Retrieves detailed information about a specific staff member by their ID.
   *
   * @param id the ID of the staff member to retrieve
   * @return a Result object containing detailed information about
   *      the staff member as a {@code StaffVo}
   */
  @LoginRequired(accountTypes = {AccountType.admin, AccountType.staff, AccountType.student})
  @GetMapping("/{id}")
  public Result<StaffVo> getStaffDetail(@PathVariable Integer id) {
    return staffService.getStaffDetail(id);
  }

  /**
   * Retrieves a list of unique departments associated with staff members.
   *
   * @return a Result object containing a list of department names
   */
  @LoginRequired(accountTypes = {AccountType.admin, AccountType.staff})
  @GetMapping("/list-departments")
  public Result<List<String>> listDepartments() {
    return staffService.listDepartments();
  }

  /**
   * Retrieves a list of unique titles associated with staff members.
   * This endpoint is accessible to authenticated users with the account types
   * {@code admin} or {@code staff}.
   *
   * @return a Result object containing a list of staff titles
   */
  @LoginRequired(accountTypes = {AccountType.admin, AccountType.staff})
  @GetMapping("/list-titles")
  public Result<List<String>> listTitles() {
    return staffService.listTitles();
  }

}
