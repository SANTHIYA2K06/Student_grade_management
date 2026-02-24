package uk.ac.ucl.comp0010.generator;

import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.ucl.comp0010.entity.Admin;
import uk.ac.ucl.comp0010.service.AdminService;
import uk.ac.ucl.comp0010.utils.PasswordEncoderUtils;

/**
 * Admin generator for Application.
 *
 * @author Jack Pan
 * @since 2024-10-23
 */
@Component
public class AdminGenerator implements CommandLineRunner {

  @Resource
  private AdminService adminService;


  /**
   * This method is called when the application starts. It generates an admin user with the
   * specified credentials.
   *
   * @param args command-line arguments
   */
  @Override
  public void run(String... args) {
    generateAdmin("admin", "123456");
  }

  /**
   * Admin generator method for Application.
   *
   * @author Jack Pan
   * @since 2024-10-23
   */
  public void generateAdmin(String username, String password) {
    Admin admin = new Admin();
    admin.setUsername(username);
    admin.setPassword(PasswordEncoderUtils.encode(password));
    adminService.save(admin);
    System.out.println("Admin user created successfully.");
    System.out.println("username:" + username);
    System.out.println("password:" + password);
  }
}
