package uk.ac.ucl.comp0010.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
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
import uk.ac.ucl.comp0010.dto.CreateRegistrationDto;
import uk.ac.ucl.comp0010.dto.EditRegistrationDto;
import uk.ac.ucl.comp0010.enums.AccountType;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.service.RegistrationService;
import uk.ac.ucl.comp0010.vo.RegistrationListVo;


/**
 * <p>
 * Registration related api controller.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-10-18
 */
@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

  @Resource
  private RegistrationService registrationService;

  /**
   * Adds a new registration based on the provided details.
   * This endpoint is accessible to authenticated users with the account types
   * {@code admin} or {@code staff}.
   *
   * @param createRegistrationDto the data transfer object
   *                              containing the details for the new registration
   * @return a Result object indicating the success or failure of the addition
   */
  @LoginRequired(accountTypes = {AccountType.admin, AccountType.staff})
  @PostMapping("/add")
  public Result<Object> add(@RequestBody CreateRegistrationDto createRegistrationDto) {
    return registrationService.addRegistration(createRegistrationDto);
  }

  /**
   * Edits an existing registration based on the provided details and ID.
   * This endpoint is accessible to authenticated users with the account types
   * {@code admin} or {@code staff}.
   *
   * @param editRegistrationDto the data transfer object containing
   *                            the updated details for the registration
   * @param id                  the ID of the registration to edit
   * @return a Result object indicating the success or failure of the operation
   */
  @LoginRequired(accountTypes = {AccountType.admin, AccountType.staff})
  @PutMapping("/edit/{id}")
  public Result<Object> edit(@RequestBody EditRegistrationDto editRegistrationDto,
      @PathVariable Long id) {
    return registrationService.editRegistration(editRegistrationDto, id);
  }

  /**
   * Retrieves a paginated list of registrations based on optional filtering criteria.
   * This endpoint is accessible to authenticated users with the account types
   * {@code admin} or {@code staff}.
   *
   * @param current   the current page number
   * @param size      the number of records per page
   * @param studentId (optional) the ID of the student to filter by
   * @param recordId  (optional) the ID of the record to filter by
   * @return a Result object containing a paginated list of {@code RegistrationListVo} objects
   */
  @LoginRequired(accountTypes = {AccountType.admin, AccountType.staff})
  @GetMapping
  public Result<IPage<RegistrationListVo>> list(@RequestParam Integer current,
      @RequestParam Integer size,
      @RequestParam(required = false) Integer studentId,
      @RequestParam(required = false) Long recordId) {
    return registrationService.list(current, size, studentId, recordId);
  }

  /**
   * Deletes a specific registration by its ID.
   * This endpoint is accessible to authenticated users with the account types
   * {@code admin} or {@code staff}.
   *
   * @param id the ID of the registration to delete
   * @return a Result object indicating the success or failure of the deletion
   */
  @LoginRequired(accountTypes = {AccountType.admin, AccountType.staff})
  @DeleteMapping("/{id}")
  public Result<Object> delete(@PathVariable Long id) {
    return registrationService.delete(id);
  }

  /**
   * Imports registrations from a provided CSV file.
   * This endpoint is accessible to authenticated users with the account types
   * {@code admin} or {@code staff}.
   *
   * @param file the CSV file containing the registration data to import
   * @return a Result object indicating the success or failure of the import operation
   */
  @LoginRequired(accountTypes = {AccountType.admin, AccountType.staff})
  @PostMapping("/import-registrations")
  public Result<Object> importRegistration(@RequestParam MultipartFile file) {
    return registrationService.importRegistrationByCsv(file);
  }

}
