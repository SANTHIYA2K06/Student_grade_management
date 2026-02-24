package uk.ac.ucl.comp0010.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ucl.comp0010.annotation.LoginRequired;
import uk.ac.ucl.comp0010.dto.ResetPasswordDto;
import uk.ac.ucl.comp0010.enums.AccountType;
import uk.ac.ucl.comp0010.enums.JwtType;
import uk.ac.ucl.comp0010.exception.CustomException;
import uk.ac.ucl.comp0010.exception.ServerException;
import uk.ac.ucl.comp0010.model.Payload;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.service.AuthenticationService;
import uk.ac.ucl.comp0010.utils.JwtUtils;
import uk.ac.ucl.comp0010.vo.LoginVo;

/**
 * <p>
 * Authentication related API controller.
 * </p>
 *
 * @author Jack Pan, Wesley Xu
 * @since 2024-10-17
 */
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

  @Resource
  private AuthenticationService authenticationService;


  /**
   * Refreshing accessToken and refreshToken.
   *
   * @author Jack Pan
   * @since 2024-10-23
   */
  @GetMapping("/refresh")
  public Result<LoginVo> refresh(@RequestParam String refreshToken) {
    Payload payload;
    try {
      payload = JwtUtils.getPayloadFromJwt(refreshToken);
    } catch (ServerException e) {
      throw new CustomException(e.getMessage());
    }
    if (!payload.getJwtType().equals(JwtType.refresh_token)) {
      throw new CustomException("Please use refresh token for refreshing");
    }
    String accessToken = JwtUtils.generateJwtToken(payload.getAccountId(), payload.getAccountType(),
        JwtType.access_token);
    String newRefreshToken = JwtUtils.generateJwtToken(payload.getAccountId(),
        payload.getAccountType(), JwtType.refresh_token);
    return Result.success(new LoginVo(accessToken, newRefreshToken));
  }

  /**
   * Reset Password.
   *
   * @author Wesley Xu
   * @since 2024-11-12
   */
  @LoginRequired(accountTypes = {AccountType.admin, AccountType.student, AccountType.staff})
  @PutMapping("/reset-password")
  public Result<Object> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
    authenticationService.resetPassword(resetPasswordDto);
    return Result.success("Password reset successfully");
  }
}
