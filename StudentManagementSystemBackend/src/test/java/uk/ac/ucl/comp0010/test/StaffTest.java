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
import uk.ac.ucl.comp0010.dto.CreateRecordDto;
import uk.ac.ucl.comp0010.dto.CreateRegistrationDto;
import uk.ac.ucl.comp0010.dto.EditRecordDto;
import uk.ac.ucl.comp0010.dto.EditRegistrationDto;
import uk.ac.ucl.comp0010.dto.LoginDto;
import uk.ac.ucl.comp0010.dto.ResetPasswordDto;
import uk.ac.ucl.comp0010.entity.Module;
import uk.ac.ucl.comp0010.entity.Record;
import uk.ac.ucl.comp0010.entity.Registration;
import uk.ac.ucl.comp0010.entity.Staff;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.service.ModuleService;
import uk.ac.ucl.comp0010.service.RecordService;
import uk.ac.ucl.comp0010.service.RegistrationService;
import uk.ac.ucl.comp0010.service.StaffService;
import uk.ac.ucl.comp0010.utils.PasswordEncoderUtils;
import uk.ac.ucl.comp0010.vo.LoginVo;
import uk.ac.ucl.comp0010.vo.StudentVo;

/**
 * Testing Staff required API.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class StaffTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ModuleService moduleService;

  @Autowired
  private RegistrationService registrationService;

  @Autowired
  private RecordService recordService;

  @Autowired
  private StaffService staffService;

  private String accessToken;


  /**
   * Create a staff for test and get logged in before testing.
   *
   * @throws Exception if any error occurs
   */
  @BeforeEach
  public void setup() throws Exception {
    Staff staff = new Staff();
    staff.setUsername("test");
    staff.setPassword(PasswordEncoderUtils.encode("test"));
    staffService.save(staff);
    LoginDto loginDto = new LoginDto();
    loginDto.setUsername("test");
    loginDto.setPassword("test");
    String userJson = objectMapper.writeValueAsString(loginDto);
    // Test Login
    MvcResult result = mockMvc.perform(
        MockMvcRequestBuilders.post("/api/staff/login").contentType(MediaType.APPLICATION_JSON)
            .content(userJson)).andExpect(status().isOk()).andReturn();
    // Get the response content as a string
    String responseContent = result.getResponse().getContentAsString();
    // Convert the response string to JSON (Map)
    Result<LoginVo> responseResult = objectMapper.readValue(responseContent,
        new TypeReference<>() {
        });
    this.accessToken = responseResult.getData().getAccessToken();
  }

  /**
   * Test the condition of logged in fails.
   *
   * @throws Exception if any error occurs
   */
  private void testLoginFail() throws Exception {
    LoginDto loginDto = new LoginDto();
    loginDto.setUsername("Error Username");
    loginDto.setPassword("Error Password");
    String userJson = objectMapper.writeValueAsString(loginDto);
    mockMvc.perform(
        MockMvcRequestBuilders.post("/api/staff/login").contentType(MediaType.APPLICATION_JSON)
            .content(userJson)).andExpect(status().is4xxClientError());
  }

  /**
   * Test listing students.
   *
   * @param accessToken access Token
   * @param current current Page
   * @param size page size
   * @throws Exception if any error occurs
   */

  private void testListStudents(String accessToken, Integer current, Integer size)
      throws Exception {
    //Test List Students
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/student/list-students")
                .header("Authorization", accessToken)
                .param("current", current.toString())
                .param("size", size.toString())
                .param("id", "1")
                .param("fullName", "test")
                .param("username", "test")
                .param("programOfStudy", "test"))
        .andExpect(status().isOk());
  }

  /**
   * Test listing programs.
   */
  private void testListPrograms() throws Exception {
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/student/list-programs")
                .header("Authorization", accessToken))
        .andExpect(status().isOk());
  }


  /**
   * Test retrieving student details by a valid ID.
   * This test checks if the API returns the correct student information
   * when provided with a valid student ID.
   * The expected result is a 200 OK status and the student details with the correct ID.
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
   * <p>
   *   Test retrieving student details with a non-existent student ID.
   *   This test verifies that the API returns a 4xx client error when
   *   attempting to retrieve details for a student ID that does not exist in the system.
   * </p>
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

  /**
   * Test List Modules.
   *
   * @param accessToken access token
   * @param current     current page
   * @param size        page size
   * @throws Exception if the request fails
   */

  private void testListModules(String accessToken, Integer current, Integer size)
      throws Exception {
    //Test List Modules
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/module/list")
                .header("Authorization", accessToken)
                .param("current", current.toString())
                .param("size", size.toString())
                .param("name", "test")
                .param("code", "test")
                .param("leader", "test"))
        .andExpect(status().isOk());
  }

  /**
   * Test List Staffs.
   *
   * @param accessToken access token
   * @param current current page
   * @param size page size
   * @throws Exception if the request fails
   */

  private void testListStaffs(String accessToken, Integer current, Integer size)
      throws Exception {
    //Test List Staffs
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/staff/list-staffs")
                .header("Authorization", accessToken)
                .param("current", current.toString())
                .param("size", size.toString())
                .param("id", "1")
                .param("fullName", "Test")
                .param("department", "test")
                .param("title", "test"))
        .andExpect(status().isOk());
  }

  /**
   * Test listing departments.
   */
  private void testListDepartments() throws Exception {
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/staff/list-departments")
                .header("Authorization", accessToken))
        .andExpect(status().isOk());
  }

  /**
   * Test listing titles.
   */
  private void testListTitles() throws Exception {
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/staff/list-titles")
                .header("Authorization", accessToken))
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
   * Test staff git their own details.
   */
  private void testPersonalDetails() throws Exception {
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/staff/detail")
                .contentType(MediaType.APPLICATION_JSON)
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
   * Test adding registration for grades.
   *
   * @param accessToken Access Token
   * @throws Exception if any error occurs
   */

  private void testAddRegistration(String accessToken) throws Exception {
    CreateRegistrationDto createRegistrationDto = new CreateRegistrationDto(1,
        1L, 0);
    String dataJson = objectMapper.writeValueAsString(createRegistrationDto);
    mockMvc.perform(
        MockMvcRequestBuilders.post("/api/registration/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dataJson)
            .header("Authorization", accessToken))
        .andExpect(status().isOk());
  }

  /**
   * test the situation that adding registration fails.
   *
   * @param accessToken Access Token
   * @throws Exception if any error occurs
   */

  private void testAddRegistrationFail(String accessToken) throws Exception {
    // test invalid record id
    CreateRegistrationDto createRegistrationDto = new CreateRegistrationDto(1,
        999L, null);
    String dataJson = objectMapper.writeValueAsString(createRegistrationDto);
    mockMvc.perform(
        MockMvcRequestBuilders.post("/api/registration/add")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dataJson).header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
    // test duplicated record
    createRegistrationDto = new CreateRegistrationDto(1,
        1L, 0);
    dataJson = objectMapper.writeValueAsString(createRegistrationDto);
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/registration/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataJson)
                .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
    // test invalid score
    createRegistrationDto = new CreateRegistrationDto(1,
        1L, -1);
    dataJson = objectMapper.writeValueAsString(createRegistrationDto);
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/registration/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataJson)
                .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
    // test student ID empty
    createRegistrationDto = new CreateRegistrationDto(null,
        1L, -1);
    dataJson = objectMapper.writeValueAsString(createRegistrationDto);
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/registration/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataJson)
                .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
    // test record ID empty
    createRegistrationDto = new CreateRegistrationDto(1,
        null, 90);
    dataJson = objectMapper.writeValueAsString(createRegistrationDto);
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/registration/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataJson)
                .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
    // test student ID invalid
    createRegistrationDto = new CreateRegistrationDto(99999,
        1L, 0);
    dataJson = objectMapper.writeValueAsString(createRegistrationDto);
    mockMvc.perform(
            MockMvcRequestBuilders.post("/api/registration/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataJson)
                .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());

  }

  /**
   * Test Editing registration.
   *
   * @param accessToken Access Token
   * @throws Exception if any error occurs
   */
  private void testEditRegistration(String accessToken) throws Exception {
    EditRegistrationDto editRegistrationDto = new EditRegistrationDto(null, null,
        90);
    String dataJson = objectMapper.writeValueAsString(editRegistrationDto);
    mockMvc.perform(
            MockMvcRequestBuilders.put("/api/registration/edit/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataJson)
                .header("Authorization", accessToken))
        .andExpect(status().isOk());
  }

  /**
   * test the situation that editing registration fails.
   *
   * @param accessToken Access Token
   * @throws Exception if any error occurs
   */
  private void testEditRegistrationFails(String accessToken) throws Exception {
    // Test using invalid student ID
    EditRegistrationDto editRegistrationDto = new EditRegistrationDto(9999, null,
        90);
    String dataJson = objectMapper.writeValueAsString(editRegistrationDto);
    mockMvc.perform(
            MockMvcRequestBuilders.put("/api/registration/edit/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataJson)
                .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());

    // Test using invalid registration ID
    editRegistrationDto = new EditRegistrationDto(null, null,
        90);
    dataJson = objectMapper.writeValueAsString(editRegistrationDto);
    mockMvc.perform(
            MockMvcRequestBuilders.put("/api/registration/edit/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataJson)
                .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());

    // Test using invalid module code
    editRegistrationDto = new EditRegistrationDto(1, 999L,
        90);
    dataJson = objectMapper.writeValueAsString(editRegistrationDto);
    mockMvc.perform(
            MockMvcRequestBuilders.put("/api/registration/edit/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataJson)
                .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
    // Test using invalid score
    editRegistrationDto = new EditRegistrationDto(null, null,
        101);
    dataJson = objectMapper.writeValueAsString(editRegistrationDto);
    mockMvc.perform(
            MockMvcRequestBuilders.put("/api/registration/edit/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataJson)
                .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
    // Test duplicated record
    recordService.save(new Record(null, "testModule", LocalDate.now()));
    registrationService.save(new Registration(new CreateRegistrationDto(1, 2L, null)));
    editRegistrationDto = new EditRegistrationDto(null, 1L,
        null);
    dataJson = objectMapper.writeValueAsString(editRegistrationDto);
    mockMvc.perform(
            MockMvcRequestBuilders.put("/api/registration/edit/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dataJson)
                .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
  }

  /**
   * Test listing registrations.
   *
   * @param accessToken Access Token.
   * @param current     current page
   * @param size        page size
   * @throws Exception if any error occurs during reset password request
   */

  private void testListRegistrations(String accessToken, Integer current, Integer size)
      throws Exception {
    //Test List Records
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/registration")
                .header("Authorization", accessToken)
                .param("current", current.toString())
                .param("size", size.toString())
                .param("studentId", "1")
                .param("recordId", "1"))
        .andExpect(status().isOk());
  }

  /**
   * Test Deleting Registration.
   *
   * @param accessToken access token
   * @throws Exception if any error occurs during reset password request
   */

  private void testDeleteRegistration(String accessToken) throws Exception {
    //Test delete record
    mockMvc.perform(
        MockMvcRequestBuilders.delete("/api/registration/1")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", accessToken)).andExpect(status().isOk());
  }

  /**
   * Test Deleting Registration Fail.
   */

  private void testDeleteRegistrationFail(String accessToken) throws Exception {
    //Test delete record
    mockMvc.perform(
        MockMvcRequestBuilders.delete("/api/registration/9999")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", accessToken)).andExpect(status().is4xxClientError());
  }


  /**
   * Test creating record.
   *
   * @param accessToken access Token
   * @throws Exception if any error occurs during reset password request
   */

  private void testCreateRecord(String accessToken) throws Exception {
    CreateRecordDto createRecordDto = new CreateRecordDto("testModule", LocalDate.now());
    String dataJson = objectMapper.writeValueAsString(createRecordDto);
    mockMvc.perform(MockMvcRequestBuilders.post("/api/record")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dataJson)
            .header("Authorization", accessToken))
        .andExpect(status().isOk());
  }

  /**
   * Test creating record with exceptions.
   */

  private void testCreateRecordFail(String accessToken) throws Exception {
    // Empty moduleCode
    CreateRecordDto createRecordDto = new CreateRecordDto("", LocalDate.now());
    String dataJson = objectMapper.writeValueAsString(createRecordDto);
    mockMvc.perform(MockMvcRequestBuilders.post("/api/record")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dataJson)
            .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
    // Invalid moduleCode
    createRecordDto = new CreateRecordDto("Invalid ModuleCode", LocalDate.now());
    dataJson = objectMapper.writeValueAsString(createRecordDto);
    mockMvc.perform(MockMvcRequestBuilders.post("/api/record")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dataJson)
            .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
    // Empty Date
    createRecordDto = new CreateRecordDto("testModule", null);
    dataJson = objectMapper.writeValueAsString(createRecordDto);
    mockMvc.perform(MockMvcRequestBuilders.post("/api/record")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dataJson)
            .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
  }

  /**
   * Test Editing Record.
   *
   * @param accessToken access Token
   * @throws Exception if any error occurs during reset password request
   */

  private void testEditRecord(String accessToken) throws Exception {
    EditRecordDto editRecordDto = new EditRecordDto("testModule", LocalDate.now());
    String dataJson = objectMapper.writeValueAsString(editRecordDto);
    mockMvc.perform(MockMvcRequestBuilders.put("/api/record/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dataJson)
            .header("Authorization", accessToken))
        .andExpect(status().isOk());
  }

  /**
   * Test Editing record with exceptions.
   */
  private void testEditRecordFail(String accessToken) throws Exception {
    // Invalid id
    EditRecordDto editRecordDto = new EditRecordDto("testModule", LocalDate.now());
    String dataJson = objectMapper.writeValueAsString(editRecordDto);
    mockMvc.perform(MockMvcRequestBuilders.put("/api/record/9999")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dataJson)
            .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
    // Invalid Module Code
    editRecordDto = new EditRecordDto("invalidModuleCode", LocalDate.now());
    dataJson = objectMapper.writeValueAsString(editRecordDto);
    mockMvc.perform(MockMvcRequestBuilders.put("/api/record/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(dataJson)
            .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
  }

  /**
   * Test Listing records.
   *
   * @param accessToken access token
   * @param current     current page
   * @param size        page size
   * @throws Exception if any error occurs during reset password request
   */
  private void testListRecords(String accessToken, Integer current, Integer size)
      throws Exception {
    //Test List Records
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/record")
                .header("Authorization", accessToken)
                .param("current", current.toString())
                .param("size", size.toString())
                .param("year", "2024")
                .param("month", "12")
                .param("day", "12")
                .param("moduleCode", "Test"))
        .andExpect(status().isOk());
  }

  /**
   * Test Get Record.
   *
   * @throws Exception if any error occurs during reset password request
   */
  private void testGetRecord() throws Exception {
    //Test Get Records
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/record/1")
                .header("Authorization", accessToken))
        .andExpect(status().isOk());
  }

  private void testGetRecordFail() throws Exception {
    //Test Get Records Fail
    mockMvc.perform(
            MockMvcRequestBuilders.get("/api/record/99999")
                .header("Authorization", accessToken))
        .andExpect(status().is4xxClientError());
  }

  /**
   * Test Deleting Record.
   *
   * @param accessToken access token
   * @throws Exception if any error occurs during reset password request
   */

  private void testDeleteRecord(String accessToken) throws Exception {
    //Test delete record
    mockMvc.perform(
        MockMvcRequestBuilders.delete("/api/record/1")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", accessToken)).andExpect(status().isOk());
  }

  /**
   * Test Deleting Record Fail.
   */

  private void testDeleteRecordFail(String accessToken) throws Exception {
    //Test delete record
    mockMvc.perform(
        MockMvcRequestBuilders.delete("/api/record/9999")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", accessToken)).andExpect(status().is4xxClientError());
  }

  /**
   * Tests importing records via a valid CSV file.
   * <p>
   * This method uploads a valid CSV file containing record data and expects the server
   * to return a success status.
   * </p>
   *
   * @param accessToken the authorization token
   * @throws Exception if the test request execution fails
   */
  private void testImportRecordByCsvFile(String accessToken) throws Exception {
    // Create a module before creating the record
    CreateModuleDto moduleDto = new CreateModuleDto();
    moduleDto.setCode("COMP0002");
    moduleDto.setName("Principle of Programming");
    moduleDto.setCredits(15);
    moduleDto.setStaffId(1);
    moduleService.add(moduleDto);
    String csvContent = """
        moduleCode,date
        COMP0002,2023-06-15
        """;
    MockMultipartFile file = getCsvMockMultipartFile(csvContent);
    mockMvc.perform(
                    MockMvcRequestBuilders.multipart("/api/record/import-records")
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
   * Tests importing an invalid or empty CSV file for record data.
   * <p>
   * This method attempts to upload a CSV file with invalid content or empty content and expects
   * the server to return a 4xx client error status.
   * </p>
   *
   * @param accessToken the authorization token
   * @throws Exception if the test request execution fails
   */
  private void testImportRecordByCsvFileFail(String accessToken) throws Exception {
    // Invalid content
    String csvContent = "Invalid content";
    MockMultipartFile file = getCsvMockMultipartFile(csvContent);
    mockMvc.perform(
                    MockMvcRequestBuilders.multipart("/api/record/import-records")
                            .file(file)
                            .with(request -> {
                              request.setMethod("POST");
                              return request;
                            })
                            .header("Authorization", accessToken)
            )
            .andExpect(status().is4xxClientError());

    // Empty content
    csvContent = "";
    file = getCsvMockMultipartFile(csvContent);
    mockMvc.perform(
                    MockMvcRequestBuilders.multipart("/api/record/import-records")
                            .file(file)
                            .with(request -> {
                              request.setMethod("POST");
                              return request;
                            })
                            .header("Authorization", accessToken)
            )
            .andExpect(status().is4xxClientError());
  }

  /**
   * Tests importing a valid CSV file for Registration data.
   * <p>
   * This method uploads a CSV file containing valid Registration data and expects the server to
   * return a success status.
   * </p>
   *
   * @param accessToken the authorization token
   * @throws Exception if the test request execution fails
   */
  private void testImportRegistrationByCsvFile(String accessToken) throws Exception {
    String csvContent = """
        studentId,recordId,score
        1,1,20
        """;
    MockMultipartFile file = getCsvMockMultipartFile(csvContent);
    mockMvc.perform(
            MockMvcRequestBuilders.multipart("/api/registration/import-registrations")
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
   * Tests importing an invalid or empty CSV file for Registration data.
   * <p>
   * This method attempts to upload a CSV file with invalid content or empty content and expects the
   * server to return a 4xx client error status.
   * </p>
   *
   * @param accessToken the authorization token
   * @throws Exception if the test request execution fails
   */
  private void testImportRegistrationByCsvFileFail(String accessToken) throws Exception {
    String csvContent = "Invalid content";
    MockMultipartFile file = getCsvMockMultipartFile(csvContent);
    mockMvc.perform(
            MockMvcRequestBuilders.multipart("/api/registration/import-registrations")
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
            MockMvcRequestBuilders.multipart("/api/registration/import-registrations")
                .file(file)
                .with(request -> {
                  request.setMethod("POST");
                  return request;
                })
                .header("Authorization", accessToken)
        )
        .andExpect(status().is4xxClientError());
  }


  /**
   * Test staff needed Api.
   *
   * @author Jack Pan
   * @since 2024-10-28
   */
  @Test
  void testStaff() throws Exception {
    testLoginFail();
    // Test List Students
    testListStudents(accessToken, 1, 10);
    // Test List Programs
    testListPrograms();
    // Test Retrieve Student Details
    testRetrieveStudentDetailsByIdSuccess(accessToken);
    // Test Retrieve Student Details Failed
    testRetrieveStudentDetailsByIdNotFound(accessToken);
    // Test Staffs get their own details
    testPersonalDetails();

    // Test List Modules
    testListModules(accessToken, 1, 10);
    // Test List Staffs
    testListStaffs(accessToken, 1, 10);
    // Test List Departments
    testListDepartments();
    // Test List Titles
    testListTitles();

    // Test Reset Password
    testResetPassword(accessToken);
    // Test Reset Password Fail
    testResetPasswordFail(accessToken);

    // Add a module for testing record and registration
    moduleService.save(new Module("testModule", "test", true, 50, 1));
    // Test add Record
    testCreateRecord(accessToken);
    // Test add Record Fail
    testCreateRecordFail(accessToken);
    // Test Edit Record
    testEditRecord(accessToken);
    // Test Edit Record Fail
    testEditRecordFail(accessToken);
    // Test List Record
    testListRecords(accessToken, 1, 10);
    // Test Get Record
    testGetRecord();
    // Test Get Record Fail
    testGetRecordFail();
    // Test Import Record by csv file
    testImportRecordByCsvFile(accessToken);
    // Test Import Record by csv file Fail
    testImportRecordByCsvFileFail(accessToken);

    // Test Add Registration
    testAddRegistration(accessToken);
    // Test Add Registration Fail
    testAddRegistrationFail(accessToken);
    // Test Edit Registration
    testEditRegistration(accessToken);
    // Test Edit Registration Fails
    testEditRegistrationFails(accessToken);
    // Test Import Registration by csv file
    testImportRegistrationByCsvFile(accessToken);
    // Test Import Registration by csv file Fail
    testImportRegistrationByCsvFileFail(accessToken);
    // Test List Registration
    testListRegistrations(accessToken, 1, 10);
    // Test Delete Registration
    testDeleteRegistration(accessToken);
    // Test Delete Registration Fails
    testDeleteRegistrationFail(accessToken);

    // Test Delete Record
    testDeleteRecord(accessToken);
    // Test Delete Record Fail
    testDeleteRecordFail(accessToken);

  }

}
