package uk.ac.ucl.comp0010.service.impl;

import jakarta.annotation.Resource;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.stereotype.Service;
import uk.ac.ucl.comp0010.aspect.LoginAspect;
import uk.ac.ucl.comp0010.dto.ResetPasswordDto;
import uk.ac.ucl.comp0010.enums.AccountType;
import uk.ac.ucl.comp0010.model.LoginEntity;
import uk.ac.ucl.comp0010.service.AdminService;
import uk.ac.ucl.comp0010.service.AuthenticationService;
import uk.ac.ucl.comp0010.service.StaffService;
import uk.ac.ucl.comp0010.service.StudentService;

/**
 * Implementation of the AuthenticationService for handling authentication-related operations.
 *
 * <p>This class provides functionality for resetting passwords based on the account type of
 * the logged-in user.
 *
 * @author Wesley Xu
 * @since 2024-11-14
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

  @Resource
  @Lazy
  private AdminService adminService;

  @Resource
  @Lazy
  private StaffService staffService;

  @Resource
  @Lazy
  private StudentService studentService;

  /**
   * Resets the password for the logged-in user based on account type.
   *
   * @param resetPasswordDto the DTO containing the new password details
   */
  @Override
  public void resetPassword(ResetPasswordDto resetPasswordDto) {
    LoginEntity loginEntity = LoginAspect.threadLocal.get();
    Integer accountId = loginEntity.getId();
    AccountType accountType = loginEntity.getAccountType();

    if (accountType == AccountType.admin) {
      adminService.resetPassword(accountId, resetPasswordDto);
    } else if (accountType == AccountType.staff) {
      staffService.resetPassword(accountId, resetPasswordDto);
    } else if (accountType == AccountType.student) {
      studentService.resetPassword(accountId, resetPasswordDto);
    }
  }
}
