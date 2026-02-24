package uk.ac.ucl.comp0010.test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ucl.comp0010.test.TestUtils.getCsvMockMultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.ac.ucl.comp0010.dto.CreateModuleDto;
import uk.ac.ucl.comp0010.dto.CreateStaffDto;
import uk.ac.ucl.comp0010.dto.CreateStudentDto;
import uk.ac.ucl.comp0010.dto.EditModuleDto;
import uk.ac.ucl.comp0010.dto.EditStaffDto;
import uk.ac.ucl.comp0010.dto.EditStudentDto;
import uk.ac.ucl.comp0010.dto.LoginDto;
import uk.ac.ucl.comp0010.dto.ResetPasswordDto;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.vo.LoginVo;
import uk.ac.ucl.comp0010.vo.StudentVo;

/**
 * Testing Admin required API.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AdminTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  private String accessToken;

  /**
   * Login Admin Account before testing.
   */

  @BeforeEach
  public void setup() throws Exception {
    LoginDto loginDto = new LoginDto();
    loginDto.setUsername("admin");
    loginDto.setPassword("123456");
    String userJson = objectMapper.writeValueAsString(loginDto);
    // Test Login
    MvcResult result = mockMvc.perform(
        MockMvcRequestBuilders.post("/api/admin/login").contentType(MediaType.APPLICATION_JSON)
            .content(userJson)).andExpect(status().isOk()).andReturn();
    // Get the response content as a string
    String responseContent = result.getResponse().getContentAsString();
    // Convert the response string to JSON (Map)
    Result<LoginVo> responseResult = objectMapper.readValue(responseContent,
        new TypeReference<>() {
        });
    this.accessToken = responseResult.getData().getAccessToken();
  }


  private void testAddStudent(String accessToken) throws Exception {
    //Test Add Student
    CreateStudentDto createStudentDto = new CreateStudentDto("test", "test", "test1", "test",
        "test@outllok.com",
        LocalDate.now(), "MEng Computer Science", 2027, "Computer Science");
    String userJson = objectMapper.writeValueAsString(createStudentDto);
    mockMvc.perform(
        MockMvcRequestBuilders.post("/api/student/add-student")
            .contentType(MediaType.APPLICATION_JSON)
            .content(userJson).header("Authorization", accessToken)).andExpect(status().isOk());
  }

  private void testAddStudentFail(String accessToken) throws Exception {
    //Test Add Student Fail
    CreateStudentDto createStudentDto;
    String userJson;
    createStudentDto = new CreateStudentDto("test", "test", "", "test",
        "test@outllok.com",
        LocalDate.now(), "MEng Computer Science", 2027, "Computer Science");
    userJson = objectMapper.writeValueAsString(createStudentDto);
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/student/add-student")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson).header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
  }

  private void testEditStudent(String accessToken) throws Exception {
    // Test Edit Student
    EditStudentDto editStudentDto = new EditStudentDto("newFirstName", "newLastName", null, "test",
        "newEmail@example.com", LocalDate.now(), "Computer Science", 2025, "Engineering");
    String studentJson = objectMapper.writeValueAsString(editStudentDto);
    mockMvc.perform(
            MockMvcRequestBuilders.put("/api/student/edit/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(studentJson)
                .header("Authorization", accessToken))
        .andExpect(status().isOk());
  }

  private void testEditStudentFail(String accessToken) throws Exception {
    // Test Edit Student Fail
    EditStudentDto editStudentDtoInvalid = new EditStudentDto(null, null, "", null, null, null,
        null, null, null);
    String studentJsonInvalid = objectMapper.writeValueAsString(editStudentDtoInvalid);
    mockMvc.perform(
            MockMvcRequestBuilders.put("/api/student/edit/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(studentJsonInvalid)
                .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());

    // Test invalid password
    editStudentDtoInvalid = new EditStudentDto(null, null, null, "", null, null,
        null, null, null);
    studentJsonInvalid = objectMapper.writeValueAsString(editStudentDtoInvalid);
    mockMvc.perform(
            MockMvcRequestBuilders.put("/api/student/edit/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(studentJsonInvalid)
                .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());

    mockMvc.perform(
            MockMvcRequestBuilders.put("/api/student/edit/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(studentJsonInvalid)
                .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
  }

  private void testListStudents(String accessToken, Integer current, Integer size)
      throws Exception {
    //Test List Students
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/student/list-students")
                .header("Authorization", accessToken)
                .param("current", current.toString())
                .param("size", size.toString()))
        .andExpect(status().isOk());
  }

  private void testDeleteStudent(String accessToken) throws Exception {
    // Test Delete Student
    mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/student/delete/2")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", accessToken))
        .andExpect(status().isOk());
  }

  private void testDeleteStudentFail(String accessToken) throws Exception {
    // Test Delete Student Fail
    mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/student/delete/1000")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
  }

  private void testImportStudentByCsvFile(String accessToken) throws Exception {
    String csvContent = """
        username,password,firstName,lastName,email,birthDate,programOfStudy,graduationYear,department
        user1,password1,John,Doe,johndoe@example.com,2000-01-01,CS,2024,Engineering
        user2,password2,Jane,Smith,janesmith@example.com,2001-02-01,Math,2025,Science""";
    MockMultipartFile file = getCsvMockMultipartFile(csvContent);
    mockMvc.perform(
            MockMvcRequestBuilders.multipart("/api/student/import-students")
                .file(file)  // upload file
                .with(request -> {
                  request.setMethod("POST"); //
                  return request;
                })
                .header("Authorization", accessToken)
        )
        .andExpect(status().isOk());
  }

  private void testImportStudentByCsvFileFail(String accessToken) throws Exception {
    String csvContent = "Invalid content";
    MockMultipartFile file = getCsvMockMultipartFile(csvContent);
    mockMvc.perform(
            MockMvcRequestBuilders.multipart("/api/student/import-students")
                .file(file)  // upload file
                .with(request -> {
                  request.setMethod("POST"); // Post method
                  return request;
                })
                .header("Authorization", accessToken)
        )
        .andExpect(status().is4xxClientError());
    csvContent = "";
    file = getCsvMockMultipartFile(csvContent);
    mockMvc.perform(
            MockMvcRequestBuilders.multipart("/api/student/import-students")
                .file(file)  // upload file
                .with(request -> {
                  request.setMethod("POST"); // 指定为 POST 方法
                  return request;
                })
                .header("Authorization", accessToken)
        )
        .andExpect(status().is4xxClientError());
  }

  /**
   * Test retrieving student details by a valid ID. This test checks if the API returns the correct
   * student information when provided with a valid student ID. The expected result is a 200 OK
   * status and the student details with the correct ID.
   *
   * @throws Exception if the request fails
   */
  public void testRetrieveStudentDetailsByIdSuccess(String accessToken) throws Exception {
    int studentId = 1; // Use a valid student ID
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/student/" + studentId)
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(result -> {
          String responseContent = result.getResponse().getContentAsString();
          Result<StudentVo> responseResult = objectMapper.readValue(responseContent,
              new TypeReference<>() {
              });
          // Further assertions on response data
          StudentVo student = responseResult.getData();
          assert student != null;
          assert student.getId() == studentId;
        });
  }

  /**
   * Test retrieving student details with a non-existent student ID. This test verifies that the API
   * returns a 4xx client error when attempting to retrieve details for a student ID that does not
   * exist in the system.
   *
   * @throws Exception if the request fails
   */
  public void testRetrieveStudentDetailsByIdNotFound(String accessToken) throws Exception {
    int nonExistentStudentId = 9999; // Use a non-existent student ID
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/student/" + nonExistentStudentId)
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
  }

  private void testLoginFail() throws Exception {
    LoginDto loginDto = new LoginDto();
    loginDto.setUsername("Error Username");
    loginDto.setPassword("Error Password");
    String userJson = objectMapper.writeValueAsString(loginDto);
    mockMvc.perform(
        MockMvcRequestBuilders.post("/api/admin/login").contentType(MediaType.APPLICATION_JSON)
            .content(userJson)).andExpect(status().is4xxClientError());
  }

  private void testAddStaff(String accessToken) throws Exception {
    //Test Add Staff
    CreateStaffDto createStaffDto = new CreateStaffDto("testUser", "testUser",
        "testUser", "testUser", "testUser@outlook.com", "Mr.", "test");
    String userJson = objectMapper.writeValueAsString(createStaffDto);
    mockMvc.perform(
        MockMvcRequestBuilders.post("/api/staff/add-staff")
            .contentType(MediaType.APPLICATION_JSON)
            .content(userJson).header("Authorization", accessToken)).andExpect(status().isOk());
  }


  private void testAddStaffFail(String accessToken) throws Exception {
    //Test Add Staff Failed
    CreateStaffDto createStaffDto = new CreateStaffDto("test", "test",
        "testUser", "testUser", "test@outlook.com", "Mr", "test");
    String userJson = objectMapper.writeValueAsString(createStaffDto);
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/staff/add-staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson).header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
  }

  private void testEditStaff(String accessToken) throws Exception {
    //Test Edit Staff
    EditStaffDto editStaffDto = new EditStaffDto("test1", null, null, null, "test", null, null);
    String userJson = objectMapper.writeValueAsString(editStaffDto);
    mockMvc.perform(
        MockMvcRequestBuilders.put("/api/staff/edit/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(userJson).header("Authorization", accessToken)).andExpect(status().isOk());
  }

  private void testEditStaffFail(String accessToken) throws Exception {
    // Test blank Username
    EditStaffDto editStaffDto = new EditStaffDto("test1", null,
        null, "", null, null, null);
    String userJson = objectMapper.writeValueAsString(editStaffDto);
    mockMvc.perform(
            MockMvcRequestBuilders.put("/api/staff/edit/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson).header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
    // Test blank password
    editStaffDto = new EditStaffDto("test1", null,
        null, null, "", null, null);
    userJson = objectMapper.writeValueAsString(editStaffDto);
    mockMvc.perform(
            MockMvcRequestBuilders.put("/api/staff/edit/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson).header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());

    mockMvc.perform(
            MockMvcRequestBuilders.put("/api/staff/edit/10000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson).header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
  }

  /**
   * Tests importing a valid CSV file for Staff data.
   * <p>
   * This method uploads a CSV file containing valid Staff data and expects the server to return a
   * success status.
   * </p>
   *
   * @param accessToken the authorization token
   * @throws Exception if the test request execution fails
   */
  private void testImportStaffByCsvFile(String accessToken) throws Exception {
    String csvContent = """
        username,password,firstName,lastName,email,title,department
        staff1,password1,John,Doe,johndoe@example.com,Mr.,Engineering
        staff2,password2,Jane,Smith,janesmith@example.com,Dr.,Science""";
    MockMultipartFile file = getCsvMockMultipartFile(csvContent);
    mockMvc.perform(
            MockMvcRequestBuilders.multipart("/api/staff/import-staffs")
                .file(file)  // upload file
                .with(request -> {
                  request.setMethod("POST");
                  return request;
                })
                .header("Authorization", accessToken)
        )
        .andExpect(status().isOk());
  }

  /**
   * Tests importing an invalid or empty CSV file for Staff data.
   * <p>
   * This method attempts to upload a CSV file with invalid content or empty content and expects the
   * server to return a 4xx client error status.
   * </p>
   *
   * @param accessToken the authorization token
   * @throws Exception if the test request execution fails
   */
  private void testImportStaffByCsvFileFail(String accessToken) throws Exception {
    String csvContent = "Invalid content";
    MockMultipartFile file = getCsvMockMultipartFile(csvContent);
    mockMvc.perform(
            MockMvcRequestBuilders.multipart("/api/staff/import-staffs")
                .file(file)
                .with(request -> {
                  request.setMethod("POST");
                  return request;
                })
                .header("Authorization", accessToken)
        )
        .andExpect(status().is4xxClientError());

    csvContent = "";
    file = getCsvMockMultipartFile(csvContent);
    mockMvc.perform(
            MockMvcRequestBuilders.multipart("/api/staff/import-staffs")
                .file(file)
                .with(request -> {
                  request.setMethod("POST");
                  return request;
                })
                .header("Authorization", accessToken)
        )
        .andExpect(status().is4xxClientError());
  }

  private void testListStaffs(String accessToken)
      throws Exception {
    //Test List Staffs
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/staff/list-staffs")
                .header("Authorization", accessToken)
                .param("current", ((Integer) 1).toString())
                .param("size", ((Integer) 10).toString()))
        .andExpect(status().isOk());
  }

  private void testDeleteStaff(String accessToken) throws Exception {
    //Test delete staff
    mockMvc.perform(
        MockMvcRequestBuilders.delete("/api/staff/delete/2")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", accessToken)).andExpect(status().isOk());
  }

  private void testDeleteStaffFail(String accessToken) throws Exception {
    //Test delete staff
    mockMvc.perform(
        MockMvcRequestBuilders.delete("/api/staff/delete/1000")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", accessToken)).andExpect(status().is4xxClientError());
  }

  /**
   * Test retrieving staff details with a valid ID. Verifies that the correct staff details are
   * returned.
   */
  void testRetrieveStaffDetailsByIdSuccess(String accessToken) throws Exception {
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/staff/1")
                .header("Authorization", accessToken))
        .andExpect(status().isOk());
  }

  /**
   * Test retrieving staff details with an invalid ID. Expects a 4xx client error response.
   */
  void testRetrieveStaffDetailsByIdNotFound(String accessToken) throws Exception {
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/staff/9999")
                .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
  }

  private void testAddModule(String accessToken) throws Exception {
    // Test Add Module
    CreateModuleDto createModuleDto = new CreateModuleDto("test", "Test", true, 50, 1);
    String userJson = objectMapper.writeValueAsString(createModuleDto);
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/module/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson).header("Authorization", accessToken))
        .andExpect(status().isOk());
  }

  private void testAddModuleFail(String accessToken) throws Exception {
    // Test Add Module
    CreateModuleDto createModuleDto = new CreateModuleDto("", "Test", true, 50, 1);
    String userJson = objectMapper.writeValueAsString(createModuleDto);
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/module/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson).header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
    createModuleDto = new CreateModuleDto("test", "Test", true, 50, 1);
    userJson = objectMapper.writeValueAsString(createModuleDto);
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/module/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson).header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
    createModuleDto = new CreateModuleDto("test1", "Test", true, 50, 10000);
    userJson = objectMapper.writeValueAsString(createModuleDto);
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/module/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson).header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
  }

  private void testEditModule(String accessToken) throws Exception {
    // Test Edit Module
    EditModuleDto editModuleDto = new EditModuleDto("test", null, 50, null);
    String userJson = objectMapper.writeValueAsString(editModuleDto);
    mockMvc.perform(
            MockMvcRequestBuilders.put("/api/module/edit/test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson).header("Authorization", accessToken))
        .andExpect(status().isOk());
  }

  private void testEditModuleFail(String accessToken) throws Exception {
    // Test Edit Module Fail
    EditModuleDto editModuleDto = new EditModuleDto("test", null, 50, null);
    String userJson = objectMapper.writeValueAsString(editModuleDto);
    mockMvc.perform(
            MockMvcRequestBuilders.put("/api/module/edit/nonExistModule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson).header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
    editModuleDto = new EditModuleDto("test", null, 50, 999);
    userJson = objectMapper.writeValueAsString(editModuleDto);
    mockMvc.perform(
            MockMvcRequestBuilders.put("/api/module/edit/test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson).header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
  }

  private void testListModules(String accessToken)
      throws Exception {
    //Test List Modules
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/module/list")
                .header("Authorization", accessToken)
                .param("current", ((Integer) 1).toString())
                .param("size", ((Integer) 10).toString()))
        .andExpect(status().isOk());
  }

  /**
   * Test Getting modules.
   *
   * @throws Exception if test fails
   */

  private void testGetModule()
      throws Exception {
    //Test Get Module
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/module/test")
                .header("Authorization", accessToken))
        .andExpect(status().isOk());
  }

  /**
   * Test Getting modules fails.
   *
   * @throws Exception if test fails
   */

  private void testGetModuleFails()
      throws Exception {
    //Test Get Module Fails
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/module/invalidCode")
                .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
  }

  private void testDeleteModule(String accessToken) throws Exception {
    //Test delete module
    mockMvc.perform(
        MockMvcRequestBuilders.delete("/api/module/delete/test")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", accessToken)).andExpect(status().isOk());
  }

  private void testDeleteModuleFail(String accessToken) throws Exception {
    //Test delete module
    mockMvc.perform(
        MockMvcRequestBuilders.delete("/api/module/delete/InvalidCode")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", accessToken)).andExpect(status().is4xxClientError());
  }

  /**
   * Test for resetting the password.
   *
   * @throws Exception if any error occurs during reset password request
   */
  private void testResetPassword(String accessToken) throws Exception {
    ResetPasswordDto resetPasswordDto = new ResetPasswordDto("123456", "new_password");
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
   * Test admin needed Api.
   *
   * @author Jack Pan
   * @since 2024-10-28
   */
  @Test
  void testAdmin() throws Exception {
    testLoginFail();

    // Test List Students
    testListStudents(accessToken, 1, 10);
    // Test Add Student
    testAddStudent(accessToken);
    // Test Add Student Fail
    testAddStudentFail(accessToken);
    // Test Edit Student
    testEditStudent(accessToken);
    // Test Edit Student fail
    testEditStudentFail(accessToken);
    // Test Import Student by Csv file
    testImportStudentByCsvFile(accessToken);
    // Test Import Student by Csv file failed
    testImportStudentByCsvFileFail(accessToken);
    // Test Update Student Csv file
    testImportStudentByCsvFile(accessToken);
    // Test List Students
    testListStudents(accessToken, 2, 5);
    // Test Delete Student
    testDeleteStudent(accessToken);
    // Test Delete Student Fail
    testDeleteStudentFail(accessToken);
    // Test Retrieve Student Details
    testRetrieveStudentDetailsByIdSuccess(accessToken);
    // Test Retrieve Student Details Fail
    testRetrieveStudentDetailsByIdNotFound(accessToken);

    // Test Add Staff
    testAddStaff(accessToken);
    // Test Add Staff Failure
    testAddStaffFail(accessToken);
    // Test Edit Staff
    testEditStaff(accessToken);
    // Test Edit Staff Fail
    testEditStaffFail(accessToken);
    // Test Import Staffs By Csv file
    testImportStaffByCsvFile(accessToken);
    // Test Import Staffs By Csv file failed
    testImportStaffByCsvFileFail(accessToken);
    // Test Update Staffs By Csv file
    testImportStaffByCsvFile(accessToken);
    // Test List Staffs
    testListStaffs(accessToken);
    // Test Delete Staff
    testDeleteStaff(accessToken);
    // Test Delete Staff Fail
    testDeleteStaffFail(accessToken);

    // Test Add Module
    testAddModule(accessToken);
    // Test Add Module Fail
    testAddModuleFail(accessToken);
    // Test Edit Staff
    testEditModule(accessToken);
    // Test Edit Staff Fail
    testEditModuleFail(accessToken);
    // Test List Modules
    testListModules(accessToken);
    // Test Get Module
    testGetModule();
    // Test Get Module Fails
    testGetModuleFails();
    // Test Delete Module
    testDeleteModule(accessToken);
    // Test Delete Module Fail
    testDeleteModuleFail(accessToken);
    // Test Retrieve Staff Details
    testRetrieveStaffDetailsByIdSuccess(accessToken);
    // Test Retrieve Staff Details Fail
    testRetrieveStaffDetailsByIdNotFound(accessToken);

    // Test Reset Password
    testResetPassword(accessToken);
    // Test Reset Password Fail
    testResetPasswordFail(accessToken);
  }

}
