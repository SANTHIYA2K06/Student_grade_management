package uk.ac.ucl.comp0010.service;

import com.baomidou.mybatisplus.extension.service.IService;
import uk.ac.ucl.comp0010.dto.LoginDto;
import uk.ac.ucl.comp0010.dto.ResetPasswordDto;
import uk.ac.ucl.comp0010.entity.Admin;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.vo.LoginVo;


/**
 * <p>
 * Service Interface for Admin.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-10-22
 */
public interface AdminService extends IService<Admin> {

  Result<LoginVo> login(LoginDto loginDto);

  void resetPassword(Integer accountId, ResetPasswordDto resetPasswordDto);
}
