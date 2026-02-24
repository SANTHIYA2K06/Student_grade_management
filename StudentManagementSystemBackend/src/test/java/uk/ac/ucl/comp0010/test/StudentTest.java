package uk.ac.ucl.comp0010.test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.ac.ucl.comp0010.dto.LoginDto;
import uk.ac.ucl.comp0010.dto.ResetPasswordDto;
import uk.ac.ucl.comp0010.entity.Student;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.service.StudentService;
import uk.ac.ucl.comp0010.utils.PasswordEncoderUtils;
import uk.ac.ucl.comp0010.vo.LoginVo;

/**
 * Testing Student required API.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class StudentTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  private StudentService studentService;

  private String accessToken;

  private String refreshToken;

  /**
   * Set up before test.
   *
   * <p>
   * Create a student and use that account to test.
   * </p>
   *
   * @throws Exception if anything going wrong
   */

  @BeforeEach
  public void setup() throws Exception {
    Student student = new Student();
    student.setUsername("test");
    student.setPassword(PasswordEncoderUtils.encode("test"));
    studentService.save(student);
    // Test Student Login
    LoginDto loginDto = new LoginDto();
    loginDto.setUsername("test");
    loginDto.setPassword("test");
    String userJson = objectMapper.writeValueAsString(loginDto);
    // Test Login
    MvcResult result = mockMvc.perform(
        MockMvcRequestBuilders.post("/api/student/login").contentType(MediaType.APPLICATION_JSON)
            .content(userJson)).andExpect(status().isOk()).andReturn();
    // Get the response content as a string
    String responseContent = result.getResponse().getContentAsString();
    // Convert the response string to JSON (Map)
    Result<LoginVo> responseResult = objectMapper.readValue(responseContent,
        new TypeReference<>() {
        });
    this.accessToken = responseResult.getData().getAccessToken();
    this.refreshToken = responseResult.getData().getRefreshToken();
  }

  private void testRefreshToken(String refreshToken) throws Exception {
    //Test RefreshToken
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/auth/refresh").param("refreshToken", refreshToken))
        .andExpect(status().isOk());
  }

  private void testRefreshTokenFailure(String refreshToken) throws Exception {
    //Test RefreshToken Fail
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/auth/refresh").param("refreshToken", refreshToken))
        .andExpect(status().is4xxClientError());
  }

  private void testLoginFail() throws Exception {
    LoginDto loginDto = new LoginDto();
    loginDto.setUsername("Error Username");
    loginDto.setPassword("Error Password");
    String userJson = objectMapper.writeValueAsString(loginDto);
    mockMvc.perform(
        MockMvcRequestBuilders.post("/api/student/login").contentType(MediaType.APPLICATION_JSON)
            .content(userJson)).andExpect(status().is4xxClientError());
  }

  private void testPersonalDetails() throws Exception {
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/student/detail")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", accessToken))
        .andExpect(status().isOk());
  }

  private void testListStudentRegistrations(Integer current, Integer size) throws Exception {
    //Test List Records
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/student/get-registrations")
                .header("Authorization", accessToken)
                .param("current", current.toString())
                .param("size", size.toString()))
        .andExpect(status().isOk());
  }

  /**
   * Test for resetting the password.
   *
   * @throws Exception if any error occurs during reset password request
   */
  private void testResetPassword(String accessToken) throws Exception {
    ResetPasswordDto resetPasswordDto = new ResetPasswordDto("test", "new_password");
    String resetPasswordJson = objectMapper.writeValueAsString(resetPasswordDto);

    mockMvc.perform(MockMvcRequestBuilders.put("/api/auth/reset-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(resetPasswordJson)
            .header("Authorization", accessToken))
        .andExpect(status().isOk());
  }

  /**
   * Test for reset password failure with incorrect old password.
   *
   * @throws Exception if any error occurs during reset password request
   */
  private void testResetPasswordFail(String accessToken) throws Exception {
    ResetPasswordDto resetPasswordDto = new ResetPasswordDto("wrong_old_password", "new_password");
    String resetPasswordJson = objectMapper.writeValueAsString(resetPasswordDto);

    mockMvc.perform(MockMvcRequestBuilders.put("/api/auth/reset-password")
            .contentType(MediaType.APPLICATION_JSON)
            .content(resetPasswordJson)
            .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
  }

  /**
   * Test student needed Api.
   *
   * @author Jack Pan
   * @since 2024-10-28
   */

  @Test
  void testStudent() throws Exception {
    testRefreshToken(refreshToken);
    testRefreshTokenFailure("Invalid Refresh Token");
    testLoginFail();

    // Test Get Personal Details
    testPersonalDetails();
    // Test Personal Records
    testListStudentRegistrations(1, 10);

    // Test Reset Password
    testResetPassword(accessToken);
    // Test Reset Password Fail
    testResetPasswordFail(accessToken);
  }

}
