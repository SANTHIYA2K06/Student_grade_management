package uk.ac.ucl.comp0010.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import java.util.Map;
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
import uk.ac.ucl.comp0010.dto.CreateRecordDto;
import uk.ac.ucl.comp0010.dto.EditRecordDto;
import uk.ac.ucl.comp0010.enums.AccountType;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.service.RecordService;
import uk.ac.ucl.comp0010.vo.RecordDetailVo;
import uk.ac.ucl.comp0010.vo.RecordListVo;

/**
 * <p>
 * Record Controller.
 * </p>
 *
 * @author Jack Pan, Wesley Xu
 * @since 2024-11-22
 */
@RestController
@RequestMapping("/api/record")
public class RecordController {

  @Resource
  private RecordService recordService;

  /**
   * Creates a new record based on the provided details.
   * This endpoint is accessible to authenticated users with the account types
   * {@code admin} or {@code staff}.
   *
   * @param createRecordDto the data transfer object containing the details for the new record
   * @return a Result object containing a map with the created record's ID
   */
  @LoginRequired(accountTypes = {AccountType.admin, AccountType.staff})
  @PostMapping
  public Result<Map<String, Long>> create(@RequestBody CreateRecordDto createRecordDto) {
    return recordService.create(createRecordDto);
  }

  /**
   * Edits an existing record based on the provided details and ID.
   * This endpoint is accessible to authenticated users with the account types
   * {@code admin} or {@code staff}.
   *
   * @param editRecordDto the data transfer object containing the updated details for the record
   * @param id            the ID of the record to edit
   * @return a Result object indicating the success or failure of the operation
   */
  @LoginRequired(accountTypes = {AccountType.admin, AccountType.staff})
  @PutMapping("/{id}")
  public Result<Object> edit(@RequestBody EditRecordDto editRecordDto, @PathVariable Long id) {
    return recordService.edit(editRecordDto, id);
  }

  /**
   * Retrieves a paginated list of records based on optional filtering criteria.
   * This endpoint is accessible to authenticated users with the account types
   * {@code admin}, {@code staff}, or {@code student}.
   *
   * @param current    the current page number
   * @param size       the number of records per page
   * @param year       (optional) the year to filter by
   * @param month      (optional) the month to filter by
   * @param day        (optional) the day to filter by
   * @param moduleCode (optional) the module code to filter by
   * @return a Result object containing a paginated list of {@code RecordListVo} objects
   */
  @LoginRequired(accountTypes = {AccountType.admin, AccountType.staff, AccountType.student})
  @GetMapping
  public Result<IPage<RecordListVo>> list(@RequestParam Integer current,
      @RequestParam Integer size,
      @RequestParam(required = false) Integer year,
      @RequestParam(required = false) Integer month,
      @RequestParam(required = false) Integer day,
      @RequestParam(required = false) String moduleCode) {
    return recordService.list(current, size, year, month, day, moduleCode);
  }

  /**
   * Retrieves the details of a specific record by its ID.
   * This endpoint is accessible to authenticated users with the account types
   * {@code admin}, {@code staff}, or {@code student}.
   *
   * @param id the unique identifier of the record to retrieve
   * @return a Result object containing the details of the record as a {@code RecordDetailVo}
   */
  @LoginRequired(accountTypes = {AccountType.admin, AccountType.staff, AccountType.student})
  @GetMapping("/{id}")
  public Result<RecordDetailVo> get(@PathVariable Long id) {
    return recordService.get(id);
  }

  /**
   * Deletes a specific record by its ID.
   * This endpoint is accessible to authenticated users with the account types
   * {@code admin} or {@code staff}.
   *
   * @param id the unique identifier of the record to delete
   * @return a Result object indicating the success or failure of the deletion operation
   */
  @LoginRequired(accountTypes = {AccountType.admin, AccountType.staff})
  @DeleteMapping("/{id}")
  public Result<Object> delete(@PathVariable Long id) {
    return recordService.delete(id);
  }

  /**
   * Imports records from a provided CSV file.
   * This endpoint is accessible to authenticated users with the account types
   * {@code admin} or {@code staff}.
   *
   * @param file the CSV file containing the records to be imported
   * @return a Result object indicating the success or failure of the import operation
   */
  @LoginRequired(accountTypes = {AccountType.admin, AccountType.staff})
  @PostMapping("/import-records")
  public Result<Object> importRecords(@RequestParam MultipartFile file) {
    return recordService.importRecordByCsv(file);
  }

}