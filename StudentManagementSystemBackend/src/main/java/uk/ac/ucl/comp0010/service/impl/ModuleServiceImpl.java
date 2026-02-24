package uk.ac.ucl.comp0010.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import uk.ac.ucl.comp0010.dto.CreateModuleDto;
import uk.ac.ucl.comp0010.dto.EditModuleDto;
import uk.ac.ucl.comp0010.entity.Module;
import uk.ac.ucl.comp0010.entity.Staff;
import uk.ac.ucl.comp0010.exception.CustomException;
import uk.ac.ucl.comp0010.mapper.ModuleMapper;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.service.ModuleService;
import uk.ac.ucl.comp0010.service.StaffService;
import uk.ac.ucl.comp0010.vo.ModuleDetailVo;
import uk.ac.ucl.comp0010.vo.ModuleListVo;


/**
 * <p>
 * Service implementation for Module.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-10-18
 */
@Service
public class ModuleServiceImpl extends ServiceImpl<ModuleMapper, Module> implements ModuleService {

  @Lazy
  @Resource
  private StaffService staffService;

  @Resource
  private ModuleMapper moduleMapper;

  /**
   * Add Modules.
   *
   * @param createModuleDto create Module data object
   * @return success
   */
  @Override
  public Result<Object> add(CreateModuleDto createModuleDto) {
    //Primary Key Check
    if (createModuleDto.getCode() == null || createModuleDto.getCode().isBlank()) {
      throw new CustomException("Code can't be null!");
    }
    // Unique Check
    Module moduleForCheck = this.getOne(
        new QueryWrapper<Module>().eq("code", createModuleDto.getCode()));
    if (moduleForCheck != null) {
      throw new CustomException("Code already exist!");
    }
    // Staff ID Check
    if (createModuleDto.getStaffId() == null) {
      throw new CustomException("Staff ID can't be null!");
    }
    if (createModuleDto.getCredits() != null && (createModuleDto.getCredits() > 100
        || createModuleDto.getCredits() < 0)) {
      throw new CustomException("Credits must between 0 and 100!");
    }
    Staff staff = staffService.getById(createModuleDto.getStaffId());
    if (staff == null) {
      throw new CustomException("No such staff!");
    }
    Module module = new Module(createModuleDto);
    this.save(module);
    return Result.success();
  }

  /**
   * Listing modules.
   *
   * @param current current page number
   * @param size    page size
   * @return page
   */
  @Override
  public Result<IPage<ModuleListVo>> list(Integer current, Integer size, String code, String name,
      String leader) {
    QueryWrapper<ModuleListVo> wrapper = new QueryWrapper<>();
    if (code != null && !code.isBlank()) {
      wrapper.like("code", code);
    }
    if (name != null && !name.isBlank()) {
      wrapper.like("name", name);
    }
    if (leader != null && !leader.isBlank()) {
      wrapper.apply("CONCAT(staff.first_name, ' ', staff.last_name) LIKE {0}",
          "%" + leader + "%");
    }
    IPage<ModuleListVo> ipage = new Page<>(current, size);
    return Result.success(moduleMapper.list(ipage, wrapper));
  }

  /**
   * Editing Modules.
   *
   * @param editModuleDto edit module data object
   * @param code          editing module code
   * @return success
   */
  @Override
  public Result<Object> edit(EditModuleDto editModuleDto, String code) {
    Module module = this.getById(code);
    if (module == null) {
      throw new CustomException("No such module!");
    }
    // Staff Check
    if (editModuleDto.getStaffId() != null) {
      Staff staffForCheck = staffService.getById(editModuleDto.getStaffId());
      if (staffForCheck == null) {
        throw new CustomException("No such staff!");
      }
    }
    // Credits Check
    if (editModuleDto.getCredits() != null) {
      if (editModuleDto.getCredits() > 100 || editModuleDto.getCredits() < 0) {
        throw new CustomException("Credits must between 0 and 100!");
      }
    }
    module.setCode(code);
    module.setName(editModuleDto.getName());
    module.setMnc(editModuleDto.getMnc());
    module.setCredits(editModuleDto.getCredits());
    module.setStaffId(editModuleDto.getStaffId());
    this.updateById(module);
    return Result.success();
  }

  /**
   * Delete module.
   *
   * @param code deleting module code
   * @return success
   */
  @Override
  public Result<Object> delete(String code) {
    Module module = this.getById(code);
    if (module == null) {
      throw new CustomException("No such module!");
    }
    this.removeById(code);
    return Result.success();
  }

  /**
   * Getting details of a module.
   *
   * @param code Module Code
   * @return ModuleDetailVo
   */
  @Override
  public Result<ModuleDetailVo> get(String code) {
    ModuleDetailVo moduleDetail = moduleMapper.get(code);
    if (moduleDetail == null) {
      throw new CustomException("No such module!");
    }
    return Result.success(moduleDetail);
  }
}
