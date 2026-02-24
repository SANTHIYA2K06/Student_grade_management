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
import uk.ac.ucl.comp0010.dto.CreateStaffDto;
import uk.ac.ucl.comp0010.dto.EditStaffDto;
import uk.ac.ucl.comp0010.dto.LoginDto;
import uk.ac.ucl.comp0010.dto.ResetPasswordDto;
import uk.ac.ucl.comp0010.entity.Staff;
import uk.ac.ucl.comp0010.enums.AccountType;
import uk.ac.ucl.comp0010.enums.JwtType;
import uk.ac.ucl.comp0010.exception.CustomException;
import uk.ac.ucl.comp0010.mapper.StaffMapper;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.service.StaffService;
import uk.ac.ucl.comp0010.utils.CsvUtils;
import uk.ac.ucl.comp0010.utils.JwtUtils;
import uk.ac.ucl.comp0010.utils.PasswordEncoderUtils;
import uk.ac.ucl.comp0010.vo.LoginVo;
import uk.ac.ucl.comp0010.vo.StaffListVo;
import uk.ac.ucl.comp0010.vo.StaffVo;


/**
 * <p>
 * Implementation for Staff Service Interface.
 * </p>
 *
 * @author Jack Pan, Wesley Xu
 * @since 2024-11-14
 */
@Service
public class StaffServiceImpl extends ServiceImpl<StaffMapper, Staff> implements
    StaffService {

  @Resource
  private StaffMapper staffMapper;

  /**
   * login staff account.
   *
   * @param loginDto Data for login
   * @return Result with data LoginVo
   * @author Wesley Xu
   * @since 2024-10-17
   */
  @Override
  public Result<LoginVo> login(LoginDto loginDto) {
    Staff staff = this.getOne(new QueryWrapper<Staff>().eq("username",
        loginDto.getUsername()));
    if (staff == null) {
      throw new CustomException("No such username");
    }
    if (!PasswordEncoderUtils.checkPassword(loginDto.getPassword(), staff.getPassword())) {
      throw new CustomException("Username or Password Incorrect");
    }
    String accessToken = JwtUtils.generateJwtToken(staff.getId(), AccountType.staff,
        JwtType.access_token);
    String refreshToken = JwtUtils.generateJwtToken(staff.getId(), AccountType.staff,
        JwtType.refresh_token);
    return Result.success(new LoginVo(accessToken, refreshToken));
  }

  /**
   * Adds a new staff member based on the provided details.
   * Performs validation checks to ensure data integrity and uniqueness of the username.
   *
   * @param createStaffDto the data transfer object containing the new staff member's details,
   *                       including username and password
   * @return a Result object indicating success
   * @throws CustomException if:
   *                         <ul>
   *                           <li>The username or password is null or blank</li>
   *                           <li>A staff member with the same username already exists</li>
   *                         </ul>
   */
  @Override
  public Result<Object> addStaff(CreateStaffDto createStaffDto) {
    // Integrity Check
    if (createStaffDto.getUsername() == null || createStaffDto.getUsername().isBlank()
        || createStaffDto.getPassword() == null || createStaffDto.getPassword().isBlank()) {
      throw new CustomException("Username or password can't be null");
    }
    // Unique Check
    Staff staff = this.getOne(
        new QueryWrapper<Staff>().eq("username", createStaffDto.getUsername()));
    if (staff != null) {
      throw new CustomException("Username already exist!");
    }
    createStaffDto.setPassword(PasswordEncoderUtils.encode(createStaffDto.getPassword()));
    staff = new Staff(createStaffDto);
    this.save(staff);
    return Result.success();
  }

  /**
   * list staffs.
   *
   * @param current current page
   * @param size    size of the page
   * @return data
   */
  @Override
  public Result<IPage<StaffListVo>> listStaffs(Integer current, Integer size, Integer id,
      String fullName, String department, String title) {
    QueryWrapper<StaffListVo> wrapper = new QueryWrapper<>();
    if (id != null) {
      wrapper.eq("id", id);
    }
    if (fullName != null && !fullName.isBlank()) {
      wrapper.apply("CONCAT(first_name, ' ', last_name) LIKE {0}",
          "%" + fullName + "%");
    }
    if (department != null && !department.isBlank()) {
      wrapper.like("department", department);
    }
    if (title != null && !title.isBlank()) {
      wrapper.like("title", title);
    }
    IPage<StaffListVo> ipage = new Page<>(current, size);
    return Result.success(staffMapper.listStaffs(ipage, wrapper));
  }

  /**
   * import staff using csv files.
   *
   * @param file csv file
   * @return success
   */
  @Transactional
  @Override
  public Result<Object> importStaff(MultipartFile file) {
    List<CreateStaffDto> records = CsvUtils.readCsv(file, CreateStaffDto.class);
    Set<String> existingUsernames = this.list().stream()
        .map(Staff::getUsername)
        .collect(Collectors.toSet());
    List<Staff> savingStaffList = new ArrayList<>();
    List<Staff> updatingStaffList = new ArrayList<>();
    for (CreateStaffDto record : records) {
      // Integrity Check
      if (record.getUsername() == null || record.getUsername().isBlank()
          || record.getPassword() == null || record.getPassword().isBlank()) {
        throw new CustomException("Username or password can't be null");
      }
      if (!existingUsernames.contains(record.getUsername())) {
        savingStaffList.add(new Staff(record));
      } else {
        updatingStaffList.add(new Staff(record));
      }
    }
    this.saveBatch(savingStaffList);
    this.updateBatchById(updatingStaffList);
    return Result.success();
  }

  /**
   * Edit staff.
   *
   * @param editStaffDto edit staff data object.
   * @param id           id of the staff being edited.
   * @return success
   */
  @Override
  public Result<Object> edit(EditStaffDto editStaffDto, Integer id) {
    Staff staff = this.getById(id);
    if (staff == null) {
      throw new CustomException("Staff not exist!");
    }
    //Integrity Check
    if (editStaffDto.getUsername() != null && editStaffDto.getUsername().isEmpty()) {
      throw new CustomException("Username can't be empty");
    }
    if (editStaffDto.getPassword() != null && editStaffDto.getPassword().isEmpty()) {
      throw new CustomException("Password can't be empty");
    }
    // Unique Check
    if (editStaffDto.getUsername() != null && !editStaffDto.getUsername()
        .equals(staff.getUsername())) {
      Staff staffForCheck = this.getOne(
          new QueryWrapper<Staff>().eq("username", editStaffDto.getUsername())
      );
      if (staffForCheck != null) {
        throw new CustomException("Username already exist!");
      }
    }
    staff.setUsername(editStaffDto.getUsername());
    staff.setFirstName(editStaffDto.getFirstName());
    staff.setLastName(editStaffDto.getLastName());
    staff.setEmail(editStaffDto.getEmail());
    staff.setTitle(editStaffDto.getTitle());
    staff.setDepartment(editStaffDto.getDepartment());
    if (editStaffDto.getPassword() != null) {
      staff.setPassword(PasswordEncoderUtils.encode(editStaffDto.getPassword()));
    }
    this.updateById(staff);
    return Result.success();
  }

  /**
   * Delete staff.
   *
   * @param id id of the staff
   * @return success
   */
  @Override
  public Result<Object> delete(Integer id) {
    Staff staff = this.getById(id);
    if (staff == null) {
      throw new CustomException("Staff not exist!");
    }
    this.removeById(id);
    return Result.success();
  }

  /**
   * Reset password.
   *
   * @param accountId id of the staff
   */
  @Override
  public void resetPassword(Integer accountId, ResetPasswordDto resetPasswordDto) {
    Staff staff = this.getById(accountId);
    if (staff == null) {
      throw new CustomException("Staff not found");
    }
    if (!PasswordEncoderUtils.checkPassword(resetPasswordDto.getOldPassword(),
        staff.getPassword())) {
      throw new CustomException("Old password is incorrect");
    }
    staff.setPassword(PasswordEncoderUtils.encode(resetPasswordDto.getNewPassword()));
    this.updateById(staff);
  }

  /**
   * Staffs get their own details.
   *
   * @return Student Value Object
   */
  @Override
  public Result<StaffVo> detail() {
    Staff staff = this.getById(LoginAspect.threadLocal.get().getId());
    return Result.success(new StaffVo(staff));
  }

  /**
   * Admin Get staff detail.
   *
   * @param id id of the staff
   * @return staff detail
   */
  @Override
  public Result<StaffVo> getStaffDetail(Integer id) {
    Staff staff = this.getById(id);
    if (staff == null) {
      throw new CustomException("Staff not found");
    }

    // Convert Staff entity to StaffVo, excluding password
    StaffVo staffVo = new StaffVo(staff);
    return Result.success(staffVo);
  }

  /**
   * Retrieves a list of unique department names associated with staff members.
   * Ensures that duplicate department names are removed from the result.
   *
   * @return a Result object containing a list of unique department names
   */
  @Override
  public Result<List<String>> listDepartments() {
    List<String> departments = staffMapper.listDepartments();
    departments = new ArrayList<>(new HashSet<>(departments));
    return Result.success(departments);
  }

  /**
   * Retrieves a list of unique job titles associated with staff members.
   * Ensures that duplicate job titles are removed from the result.
   *
   * @return a Result object containing a list of unique job titles
   */
  @Override
  public Result<List<String>> listTitles() {
    List<String> titles = staffMapper.listTitles();
    titles = new ArrayList<>(new HashSet<>(titles));
    return Result.success(titles);
  }


}
