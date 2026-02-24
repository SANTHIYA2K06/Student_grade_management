package uk.ac.ucl.comp0010;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application.
 *
 * @author Jack Pan
 * @since 2024-10-17
 */

@MapperScan("uk.ac.ucl.comp0010.mapper")
@SpringBootApplication(scanBasePackages = "uk.ac.ucl.comp0010")
public class StudentManagementSystemApplication {

  public static void main(String[] args) {
    SpringApplication.run(StudentManagementSystemApplication.class, args);
  }

}
