package uk.ac.ucl.comp0010.test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import uk.ac.ucl.comp0010.dto.CreateStaffDto;
import uk.ac.ucl.comp0010.dto.LoginDto;
import uk.ac.ucl.comp0010.entity.Admin;
import uk.ac.ucl.comp0010.entity.Staff;
import uk.ac.ucl.comp0010.entity.Student;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.service.AdminService;
import uk.ac.ucl.comp0010.service.StaffService;
import uk.ac.ucl.comp0010.service.StudentService;
import uk.ac.ucl.comp0010.utils.PasswordEncoderUtils;
import uk.ac.ucl.comp0010.vo.LoginVo;

/**
 * Testing for authorization.
 *
 * @author Jack Pan
 * @since 2024-11-12
 */

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorizationTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  private AdminService adminService;

  @Autowired
  private StaffService staffService;

  @Autowired
  private StudentService studentService;

  // Student login details
  private LoginVo studentLoginVo;


  private LoginVo login(String url, String username, String password) throws Exception {
    LoginDto loginDto = new LoginDto();
    loginDto.setUsername(username);
    loginDto.setPassword(password);
    String userJson = objectMapper.writeValueAsString(loginDto);
    // Test Login
    MvcResult result = mockMvc.perform(
        MockMvcRequestBuilders.post(url).contentType(MediaType.APPLICATION_JSON)
            .content(userJson)).andExpect(status().isOk()).andReturn();
    // Get the response content as a string
    String responseContent = result.getResponse().getContentAsString();
    // Convert the response string to JSON (Map)
    Result<LoginVo> responseResult = objectMapper.readValue(responseContent,
        new TypeReference<>() {
        });
    return responseResult.getData();
  }

  private void testNoPermissions() throws Exception {
    //Test Add Staff
    CreateStaffDto createStaffDto = new CreateStaffDto("testUser", "testUser",
        "testUser", "testUser", "testUser@outlook.com", "Mr.", "test");
    String userJson = objectMapper.writeValueAsString(createStaffDto);
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/staff/add-staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson).header("Authorization", studentLoginVo.getAccessToken()))
        .andExpect(status().isForbidden());
  }

  private void testUsingRefreshToken() throws Exception {
    //Test Add Staff
    CreateStaffDto createStaffDto = new CreateStaffDto("testUser", "testUser",
        "testUser", "testUser", "testUser@outlook.com", "Mr.", "test");
    String userJson = objectMapper.writeValueAsString(createStaffDto);
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/staff/add-staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson).header("Authorization", studentLoginVo.getRefreshToken()))
        .andExpect(status().isUnauthorized());
  }

  private void testUsingInvalidAccessToken() throws Exception {
    //Test Add Staff
    CreateStaffDto createStaffDto = new CreateStaffDto("testUser", "testUser",
        "testUser", "testUser", "testUser@outlook.com", "Mr.", "test");
    String userJson = objectMapper.writeValueAsString(createStaffDto);
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/staff/add-staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson).header("Authorization", "Invalid Token"))
        .andExpect(status().isUnauthorized());
  }

  private void testUsingEmptyAccessToken() throws Exception {
    //Test Add Staff
    CreateStaffDto createStaffDto = new CreateStaffDto("testUser", "testUser",
        "testUser", "testUser", "testUser@outlook.com", "Mr.", "test");
    String userJson = objectMapper.writeValueAsString(createStaffDto);
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/staff/add-staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson).header("Authorization", ""))
        .andExpect(status().isUnauthorized());
  }

  /**
   * Test using a JWT that is no longer valid, because the account has been deleted.
   */

  private void testUsingDeletedAdminAccessToken() throws Exception {
    // Create Test Admin
    Admin admin = new Admin();
    admin.setUsername("testAdmin");
    admin.setPassword(PasswordEncoderUtils.encode("testPassword"));
    adminService.save(admin);
    // Login
    LoginVo loginVo = login("/api/admin/login", "testAdmin", "testPassword");
    // Delete Admin
    adminService.remove(new QueryWrapper<Admin>().eq("username", "testAdmin"));
    //Test Add Staff
    CreateStaffDto createStaffDto = new CreateStaffDto("testUser", "testUser",
        "testUser", "testUser", "testUser@outlook.com", "Mr.", "test");
    String userJson = objectMapper.writeValueAsString(createStaffDto);
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/staff/add-staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson).header("Authorization", loginVo.getAccessToken()))
        .andExpect(status().isUnauthorized());
  }

  /**
   * Test using a JWT that is no longer valid, because the account has been deleted.
   */
  private void testUsingDeletedStaffAccessToken() throws Exception {
    // Create Test Staff
    Staff staff = new Staff();
    staff.setUsername("testStaff");
    staff.setPassword(PasswordEncoderUtils.encode("testPassword"));
    staffService.save(staff);
    // Login
    LoginVo loginVo = login("/api/staff/login", "testStaff", "testPassword");
    // Delete Admin
    staffService.remove(new QueryWrapper<Staff>().eq("username", "testStaff"));
    //Test List Records
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/record")
                .header("Authorization", loginVo.getAccessToken())
                .param("current", "1")
                .param("size", "10"))
        .andExpect(status().isUnauthorized());
  }

  /**
   * Test using a JWT that is no longer valid, because the account has been deleted.
   */
  private void testUsingDeletedStudentsAccessToken() throws Exception {
    // Create Test Student
    Student student = new Student();
    student.setUsername("testStudent");
    student.setPassword(PasswordEncoderUtils.encode("testPassword"));
    studentService.save(student);
    // Login
    LoginVo loginVo = login("/api/student/login", "testStudent", "testPassword");
    // Delete Admin
    studentService.remove(new QueryWrapper<Student>().eq("username", "testStudent"));
    //Test List Records
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/record")
                .header("Authorization", loginVo.getAccessToken())
                .param("current", "1")
                .param("size", "10"))
        .andExpect(status().isUnauthorized());
  }

  private void testRefreshToken() throws Exception {
    //Test RefreshToken
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/auth/refresh").param("refreshToken",
                studentLoginVo.getRefreshToken()))
        .andExpect(status().isOk());
  }

  private void testRefreshTokenFailure() throws Exception {
    //Test RefreshToken using invalid refresh Token
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/auth/refresh").param("refreshToken",
                "Invalid Refresh Token"))
        .andExpect(status().is4xxClientError());
    // Test Refresh Toke using accessToken
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/auth/refresh").param("refreshToken",
                studentLoginVo.getAccessToken()))
        .andExpect(status().is4xxClientError());
  }

  /**
   * Generate a student for test and login that account.
   *
   * @throws Exception if anything going wrong
   */
  @BeforeEach
  public void setup() throws Exception {
    Student student = new Student();
    student.setUsername("testAStudent");
    student.setPassword(PasswordEncoderUtils.encode("test"));
    studentService.save(student);
    studentLoginVo = login("/api/student/login", "testAStudent", "test");
  }

  @Test
  void testAuthorization() throws Exception {
    testRefreshToken();
    testRefreshTokenFailure();
    testNoPermissions();
    testUsingRefreshToken();
    testUsingInvalidAccessToken();
    testUsingEmptyAccessToken();
    testUsingDeletedAdminAccessToken();
    testUsingDeletedStaffAccessToken();
    testUsingDeletedStudentsAccessToken();
  }

}
