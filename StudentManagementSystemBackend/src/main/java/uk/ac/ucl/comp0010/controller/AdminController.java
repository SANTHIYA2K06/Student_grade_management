package uk.ac.ucl.comp0010.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ucl.comp0010.dto.LoginDto;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.service.AdminService;
import uk.ac.ucl.comp0010.vo.LoginVo;

/**
 * <p>
 * Admin Controller.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-10-22
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

  @Resource
  private AdminService adminService;

  /**
   * Admin Login.
   *
   * @param loginDto login data object
   * @return accessToken and refreshToken
   */
  @PostMapping("/login")
  public Result<LoginVo> login(@RequestBody LoginDto loginDto) {
    return adminService.login(loginDto);
  }

}