package uk.ac.ucl.comp0010.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ucl.comp0010.dto.CreateStaffDto;
import uk.ac.ucl.comp0010.dto.EditStaffDto;
import uk.ac.ucl.comp0010.dto.LoginDto;
import uk.ac.ucl.comp0010.dto.ResetPasswordDto;
import uk.ac.ucl.comp0010.entity.Staff;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.vo.LoginVo;
import uk.ac.ucl.comp0010.vo.StaffListVo;
import uk.ac.ucl.comp0010.vo.StaffVo;


/**
 * <p>
 * Service Interface for Staff.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-10-18
 */
public interface StaffService extends IService<Staff> {

  Result<LoginVo> login(LoginDto loginDto);

  Result<Object> addStaff(CreateStaffDto createStaffDto);

  Result<IPage<StaffListVo>> listStaffs(Integer current, Integer size, Integer id, String fullName,
      String department, String title);

  Result<Object> importStaff(MultipartFile file);

  Result<Object> edit(EditStaffDto editStaffDto, Integer id);

  Result<Object> delete(Integer id);

  void resetPassword(Integer accountId, ResetPasswordDto resetPasswordDto);

  Result<StaffVo> detail();

  Result<StaffVo> getStaffDetail(Integer id);

  Result<List<String>> listDepartments();

  Result<List<String>> listTitles();
}
