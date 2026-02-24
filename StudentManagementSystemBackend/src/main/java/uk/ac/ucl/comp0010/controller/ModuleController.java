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
import uk.ac.ucl.comp0010.annotation.LoginRequired;
import uk.ac.ucl.comp0010.dto.CreateModuleDto;
import uk.ac.ucl.comp0010.dto.EditModuleDto;
import uk.ac.ucl.comp0010.enums.AccountType;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.service.ModuleService;
import uk.ac.ucl.comp0010.vo.ModuleDetailVo;
import uk.ac.ucl.comp0010.vo.ModuleListVo;


/**
 * <p>
 * Module Controller.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-10-18
 */
@RestController
@RequestMapping("/api/module")
public class ModuleController {

  @Resource
  private ModuleService moduleService;

  /**
   * Adds a new module based on the provided data.
   * This endpoint is accessible only to authenticated users with the account type {@code admin}.
   *
   * @param createModuleDto the data transfer object containing the details of the module to add
   * @return a Result object indicating success or failure
   */
  @LoginRequired(accountTypes = {AccountType.admin})
  @PostMapping("/add")
  public Result<Object> add(@RequestBody CreateModuleDto createModuleDto) {
    return moduleService.add(createModuleDto);
  }

  /**
   * Retrieves a paginated list of modules based on optional filtering criteria.
   * This endpoint is accessible to authenticated users with the account types
   * {@code admin}, {@code staff}, or {@code student}.
   *
   * @param current the current page number
   * @param size    the number of records per page
   * @param code    (optional) the code of the module to filter by
   * @param name    (optional) the name of the module to filter by
   * @param leader  (optional) the leader of the module to filter by
   * @return a Result object containing a paginated list of {@code ModuleListVo} objects
   */
  @LoginRequired(accountTypes = {AccountType.admin, AccountType.staff, AccountType.student})
  @GetMapping("/list")
  public Result<IPage<ModuleListVo>> list(@RequestParam Integer current,
      @RequestParam Integer size,
      @RequestParam(required = false) String code,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String leader) {
    return moduleService.list(current, size, code, name, leader);
  }

  /**
   * Retrieves the details of a specific module by its code.
   * This endpoint is accessible to authenticated users with the account types
   * {@code admin}, {@code staff}, or {@code student}.
   *
   * @param code the unique code of the module to retrieve
   * @return a Result object containing the details of the module as a {@code ModuleDetailVo}
   */
  @LoginRequired(accountTypes = {AccountType.admin, AccountType.staff, AccountType.student})
  @GetMapping("/{code}")
  public Result<ModuleDetailVo> get(@PathVariable String code) {
    return moduleService.get(code);
  }

  /**
   * Edit module.
   *
   * @param editModuleDto Edit module data object
   * @param code          Module Code that is being editing
   * @return success
   */
  @LoginRequired(accountTypes = {AccountType.admin})
  @PutMapping("/edit/{code}")
  public Result<Object> edit(@RequestBody EditModuleDto editModuleDto, @PathVariable String code) {
    return moduleService.edit(editModuleDto, code);
  }

  /**
   * Delete Module.
   *
   * @param code module code that is being deleting
   * @return success
   */
  @LoginRequired(accountTypes = {AccountType.admin})
  @DeleteMapping("/delete/{code}")
  public Result<Object> delete(@PathVariable String code) {
    return moduleService.delete(code);
  }

}
