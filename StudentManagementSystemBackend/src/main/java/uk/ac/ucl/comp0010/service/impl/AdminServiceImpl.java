package uk.ac.ucl.comp0010.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import uk.ac.ucl.comp0010.dto.LoginDto;
import uk.ac.ucl.comp0010.dto.ResetPasswordDto;
import uk.ac.ucl.comp0010.entity.Admin;
import uk.ac.ucl.comp0010.enums.AccountType;
import uk.ac.ucl.comp0010.enums.JwtType;
import uk.ac.ucl.comp0010.exception.CustomException;
import uk.ac.ucl.comp0010.mapper.AdminMapper;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.service.AdminService;
import uk.ac.ucl.comp0010.utils.JwtUtils;
import uk.ac.ucl.comp0010.utils.PasswordEncoderUtils;
import uk.ac.ucl.comp0010.vo.LoginVo;


/**
 * <p>
 * Implementation for Admin Service Interface.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-10-22
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

  /**
   * login admin account.
   *
   * @param loginDto Data for login
   * @return Result with data LoginVo
   * @author Jack Pan
   * @since 2024-10-22
   */
  @Override
  public Result<LoginVo> login(LoginDto loginDto) {
    Admin admin = this.getOne(new QueryWrapper<Admin>().eq("username", loginDto.getUsername()));
    if (admin == null) {
      throw new CustomException("No such username");
    }
    if (!PasswordEncoderUtils.checkPassword(loginDto.getPassword(), admin.getPassword())) {
      throw new CustomException("Username or password incorrect");
    }
    String accessToken = JwtUtils.generateJwtToken(admin.getId(), AccountType.admin,
        JwtType.access_token);
    String refreshToken = JwtUtils.generateJwtToken(admin.getId(), AccountType.admin,
        JwtType.refresh_token);
    return Result.success(new LoginVo(accessToken, refreshToken));
  }

  /**
   * Resets the password for an admin account.
   * Validates the old password before updating it to the new password.
   *
   * @param accountId         the ID of the admin account whose password is to be reset
   * @param resetPasswordDto  the data transfer object containing the old and new passwords
   * @throws CustomException if:
   *                         <ul>
   *                           <li>The admin account with the given ID does not exist</li>
   *                           <li>The old password provided does not match the stored password</li>
   *                         </ul>
   */
  @Override
  public void resetPassword(Integer accountId, ResetPasswordDto resetPasswordDto) {
    Admin admin = this.getById(accountId);
    if (admin == null) {
      throw new CustomException("Admin not found");
    }
    if (!PasswordEncoderUtils
        .checkPassword(resetPasswordDto.getOldPassword(), admin.getPassword())) {
      throw new CustomException("Old password is incorrect");
    }
    admin.setPassword(PasswordEncoderUtils.encode(resetPasswordDto.getNewPassword()));
    this.updateById(admin);
  }

}
