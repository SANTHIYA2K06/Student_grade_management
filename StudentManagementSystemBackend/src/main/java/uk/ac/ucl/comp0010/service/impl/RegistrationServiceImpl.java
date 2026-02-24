package uk.ac.ucl.comp0010.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ucl.comp0010.dto.CreateRegistrationDto;
import uk.ac.ucl.comp0010.dto.EditRegistrationDto;
import uk.ac.ucl.comp0010.entity.Record;
import uk.ac.ucl.comp0010.entity.Registration;
import uk.ac.ucl.comp0010.entity.Student;
import uk.ac.ucl.comp0010.exception.CustomException;
import uk.ac.ucl.comp0010.mapper.RegistrationMapper;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.service.RecordService;
import uk.ac.ucl.comp0010.service.RegistrationService;
import uk.ac.ucl.comp0010.service.StudentService;
import uk.ac.ucl.comp0010.utils.CsvUtils;
import uk.ac.ucl.comp0010.vo.RegistrationListVo;


/**
 * <p>
 * Service Implementation for registration.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-10-18
 */
@Service
public class RegistrationServiceImpl extends ServiceImpl<RegistrationMapper, Registration>
    implements RegistrationService {

  @Lazy
  @Resource
  private StudentService studentService;

  @Lazy
  @Resource
  private RecordService recordService;

  @Resource
  private RegistrationMapper registrationMapper;

  /**
   * create Registration.
   *
   * @param createRegistrationDto registration detail
   * @return success
   */
  @Override
  public Result<Object> addRegistration(CreateRegistrationDto createRegistrationDto) {
    // Integrity Check
    if (createRegistrationDto.getStudentId() == null) {
      throw new CustomException("Student ID can't be null!");
    }
    if (createRegistrationDto.getRecordId() == null) {
      throw new CustomException("Record ID can't be null!");
    }
    // Student Check
    Student studentForCheck = studentService.getById(createRegistrationDto.getStudentId());
    if (studentForCheck == null) {
      throw new CustomException("No such student!");
    }
    // Record Check
    Record recordForCheck = recordService.getById(createRegistrationDto.getRecordId());
    if (recordForCheck == null) {
      throw new CustomException("No such record!");
    }

    //Score Check
    if (createRegistrationDto.getScore() != null
        && (createRegistrationDto.getScore() < 0 || createRegistrationDto.getScore() > 100)) {
      throw new CustomException("Score must between 0 and 100!");
    }
    // Unique Score Record Check
    Registration registrationForCheck = this.getOne(new QueryWrapper<Registration>()
        .eq("record_id", createRegistrationDto.getRecordId())
        .eq("student_id", createRegistrationDto.getStudentId()));
    if (registrationForCheck != null) {
      throw new CustomException(
          "Another registration with same student ID and record ID has already exist!");
    }
    // Create registration
    Registration registration = new Registration(createRegistrationDto);
    this.save(registration);
    return Result.success();
  }

  /**
   * Edits a registration record by updating its details based on the provided data.
   * Performs validation checks on the input data to ensure the integrity of the update.
   *
   * @param editRegistrationDto the data transfer object containing the updated registration details
   * @param id                  the ID of the registration record to edit
   * @return a Result object indicating success
   * @throws CustomException if:
   *                         <ul>
   *                           <li>The registration record with
   *                                the given ID does not exist</li>
   *                           <li>The specified record ID
   *                                 does not correspond to an existing record</li>
   *                           <li>The specified student ID
   *                                 does not correspond to an existing student</li>
   *                           <li>The score is not between 0 and 100</li>
   *                           <li>Another registration with
   *                                  the same student ID and record ID already exists</li>
   *                         </ul>
   */
  @Override
  public Result<Object> editRegistration(EditRegistrationDto editRegistrationDto, Long id) {
    // ID check
    Registration registration = this.getById(id);
    if (registration == null) {
      throw new CustomException("Registration record doesn't exist!");
    }
    // Record Check
    if (editRegistrationDto.getRecordId() != null) {
      Record recordForCheck = recordService.getById(editRegistrationDto.getRecordId());
      if (recordForCheck == null) {
        throw new CustomException("No such record!");
      }
    }
    // Student Check
    if (editRegistrationDto.getStudentId() != null) {
      Student studentForCheck = studentService.getById(editRegistrationDto.getStudentId());
      if (studentForCheck == null) {
        throw new CustomException("No such student!");
      }
    }
    // Score Check
    if (editRegistrationDto.getScore() != null) {
      if (editRegistrationDto.getScore() < 0 || editRegistrationDto.getScore() > 100) {
        throw new CustomException("Score must between 0 and 100!");
      }
    }
    // Unique Score Registration Check
    if (editRegistrationDto.getStudentId() != null
        || editRegistrationDto.getRecordId() != null) {
      QueryWrapper<Registration> wrapper = new QueryWrapper<>();
      if (editRegistrationDto.getStudentId() == null) {
        wrapper.eq("student_id", registration.getStudentId());
      } else {
        wrapper.eq("student_id", editRegistrationDto.getStudentId());
      }
      if (editRegistrationDto.getRecordId() == null) {
        wrapper.eq("record_id", registration.getRecordId());
      } else {
        wrapper.eq("record_id", editRegistrationDto.getRecordId());
      }
      Registration registrationForCheck = this.getOne(wrapper);
      if (registrationForCheck != null && !registrationForCheck.getId().equals(id)) {
        throw new CustomException(
            "Another registration with same student ID and record ID has already exist!");
      }
    }
    registration.setRecordId(editRegistrationDto.getRecordId());
    registration.setStudentId(editRegistrationDto.getStudentId());
    registration.setScore(editRegistrationDto.getScore());
    this.updateById(registration);
    return Result.success();
  }

  /**
   * Retrieves a paginated list of registration records based on optional filtering criteria.
   *
   * @param current   the current page number
   * @param size      the number of records per page
   * @param studentId the ID of the student to filter by (optional)
   * @param recordId  the ID of the record to filter by (optional)
   * @return a Result object containing a paginated list of {@code RegistrationListVo} objects
   */
  @Override
  public Result<IPage<RegistrationListVo>> list(Integer current, Integer size, Integer studentId,
      Long recordId) {
    QueryWrapper<RegistrationListVo> wrapper = new QueryWrapper<>();
    if (studentId != null) {
      wrapper.eq("student_id", studentId);
    }
    if (recordId != null) {
      wrapper.eq("record_id", recordId);
    }
    return Result.success(registrationMapper.list(new Page<>(current, size), wrapper));
  }

  /**
   * Deletes a registration record by its ID.
   *
   * @param id the ID of the registration to delete
   * @return a Result object indicating success
   * @throws CustomException if no registration is found with the given ID
   */
  @Override
  public Result<Object> delete(Long id) {
    Registration registration = this.getById(id);
    if (registration == null) {
      throw new CustomException("No such registration");
    }
    this.removeById(registration);
    return Result.success();
  }

  /**
   * Import Registration By csv file.
   *
   * @param file File
   * @return success
   */
  @Transactional
  @Override
  public Result<Object> importRegistrationByCsv(MultipartFile file) {
    List<CreateRegistrationDto> records = CsvUtils.readCsv(file, CreateRegistrationDto.class);
    // Get existing student IDs
    List<Integer> existingStudentIds = studentService.list().stream()
        .map(Student::getId).toList();
    // Get existing record IDs
    List<Long> existingRecordIds = recordService.list().stream()
        .map(Record::getId).toList();
    // Get existing registrations (Key:of record ID and Student ID, Value: registration ID)
    Map<String, Long> existingRegistration = new HashMap<>();
    this.list().forEach(registration -> {
      String key = registration.getRecordId().toString() + "-" + registration.getStudentId()
          .toString();
      Long value = registration.getId();
      existingRegistration.put(key, value);
    });

    List<Registration> updatingRegistration = new ArrayList<>();
    List<Registration> newRegistration = new ArrayList<>();
    for (CreateRegistrationDto registrationDto : records) {
      // Check Data
      if (registrationDto.getRecordId() == null || registrationDto.getStudentId() == null) {
        throw new CustomException("Record ID or Student ID can't be null!");
      }
      if (!existingStudentIds.contains(registrationDto.getStudentId())) {
        throw new CustomException("No such student ID");
      }
      if (!existingRecordIds.contains(registrationDto.getRecordId())) {
        throw new CustomException("No such record ID");
      }
      if (registrationDto.getScore() != null
          && (registrationDto.getScore() < 0 || registrationDto.getScore() > 100)) {
        throw new CustomException("Score must between 0 and 100!");
      }
      Registration registration = new Registration(registrationDto);
      // If there is existing registration with same record ID and student ID, then update it,
      // otherwise create a new one
      Long registrationId = existingRegistration.get(registration.getRecordId().toString()
          + "-" + registration.getStudentId().toString());
      if (registrationId == null) {
        newRegistration.add(registration);
      } else {
        registration.setId(registrationId);
        updatingRegistration.add(registration);
      }
    }
    this.saveBatch(newRegistration);
    this.updateBatchById(updatingRegistration);
    return Result.success();
  }

}
