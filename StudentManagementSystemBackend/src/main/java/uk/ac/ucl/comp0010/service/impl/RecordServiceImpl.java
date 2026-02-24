package uk.ac.ucl.comp0010.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ucl.comp0010.dto.CreateRecordDto;
import uk.ac.ucl.comp0010.dto.EditRecordDto;
import uk.ac.ucl.comp0010.entity.Module;
import uk.ac.ucl.comp0010.entity.Record;
import uk.ac.ucl.comp0010.exception.CustomException;
import uk.ac.ucl.comp0010.mapper.RecordMapper;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.service.ModuleService;
import uk.ac.ucl.comp0010.service.RecordService;
import uk.ac.ucl.comp0010.utils.CsvUtils;
import uk.ac.ucl.comp0010.vo.RecordDetailVo;
import uk.ac.ucl.comp0010.vo.RecordListVo;


/**
 * <p>
 * Service Implementation for record.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-11-22
 */
@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record>
    implements RecordService {

  @Lazy
  @Resource
  private ModuleService moduleService;

  @Resource
  private RecordMapper recordMapper;

  /**
   * Create Record.
   *
   * @param createRecordDto record detail
   * @return success
   */
  @Override
  public Result<Map<String, Long>> create(CreateRecordDto createRecordDto) {
    // Module Check
    if (createRecordDto.getModuleCode() == null || createRecordDto.getModuleCode().isBlank()) {
      throw new CustomException("Module Code can't be null!");
    }
    Module moduleForCheck = moduleService.getById(createRecordDto.getModuleCode());
    if (moduleForCheck == null) {
      throw new CustomException("No such module");
    }
    // Date Check
    if (createRecordDto.getDate() == null) {
      throw new CustomException("Date can't be null!");
    }
    Record record = new Record(createRecordDto);
    this.save(record);
    Map<String, Long> result = new HashMap<>();
    result.put("id", record.getId());
    return Result.success(result);
  }

  /**
   * Editing record.
   *
   * @param editRecordDto record details
   * @param id            ID
   * @return success
   */
  @Override
  public Result<Object> edit(EditRecordDto editRecordDto, Long id) {
    Record record = this.getById(id);
    if (record == null) {
      throw new CustomException("No such record!");
    }
    if (editRecordDto.getModuleCode() != null) {
      // Module Check
      Module moduleForCheck = moduleService.getById(editRecordDto.getModuleCode());
      if (moduleForCheck == null) {
        throw new CustomException("No such module");
      }
      record.setModuleCode(editRecordDto.getModuleCode());
    }
    if (editRecordDto.getDate() != null) {
      record.setDate(editRecordDto.getDate());
    }
    this.updateById(record);
    return Result.success();
  }

  /**
   * Listing records.
   *
   * @param current current page number
   * @param size    page size
   * @return record data.
   */
  @Override
  public Result<IPage<RecordListVo>> list(Integer current, Integer size, Integer year,
      Integer month, Integer day, String moduleCode) {
    QueryWrapper<RecordListVo> wrapper = new QueryWrapper<>();
    if (year != null) {
      wrapper.apply("YEAR(date) = {0}", year);
    }
    if (month != null) {
      wrapper.apply("MONTH(date) = {0}", month);
    }
    if (day != null) {
      wrapper.apply("DAY(date) = {0}", day);
    }
    if (moduleCode != null && !moduleCode.isBlank()) {
      wrapper.like("module_code", moduleCode);
    }
    return Result.success(recordMapper.list(new Page<>(current, size), wrapper));
  }

  /**
   * Deletes a specific record by its ID.
   *
   * @param id the ID of the record to delete
   * @return a Result object indicating success
   * @throws CustomException if no record is found with the given ID
   */
  @Override
  public Result<Object> delete(Long id) {
    Record record = this.getById(id);
    if (record == null) {
      throw new CustomException("No such record!");
    }
    this.removeById(record);
    return Result.success();
  }

  /**
   * Retrieves the details of a specific record by its ID.
   *
   * @param id the ID of the record to retrieve
   * @return a Result object containing the details of the record as a {@code RecordDetailVo}
   * @throws CustomException if no record is found with the given ID
   */
  @Override
  public Result<RecordDetailVo> get(Long id) {
    RecordDetailVo recordDetail = recordMapper.get(id);
    if (recordDetail == null) {
      throw new CustomException("No such record!");
    }
    return Result.success(recordDetail);
  }

  /**
   * Import or update records using a CSV file.
   *
   * @param file CSV file containing record data
   * @return success
   */
  @Transactional
  public Result<Object> importRecordByCsv(MultipartFile file) {
    List<CreateRecordDto> records = CsvUtils.readCsv(file, CreateRecordDto.class);
    Set<String> existingModuleCodes = moduleService.list().stream()
        .map(Module::getCode)
        .collect(Collectors.toSet());
    List<Record> newRecords = new ArrayList<>();
    for (CreateRecordDto recordDto : records) {
      if (recordDto.getModuleCode() == null || recordDto.getModuleCode().isBlank()
          || recordDto.getDate() == null) {
        throw new CustomException("Module code or date cannot be null.");
      }
      if (!existingModuleCodes.contains(recordDto.getModuleCode())) {
        throw new CustomException("No such module!");
      }
      newRecords.add(new Record(recordDto));
    }
    this.saveBatch(newRecords);
    return Result.success();
  }

}
