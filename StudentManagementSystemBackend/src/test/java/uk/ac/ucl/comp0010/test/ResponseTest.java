package uk.ac.ucl.comp0010.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.response.ResultCode;

/**
 * Test response format Data Object.
 *
 * @author Jack Pan
 * @since 2024-12-03
 */
public class ResponseTest {

  @Test
  public void testSuccessResult() {
    Result<Object> result = Result.success("Good Service!");
    assertEquals("Good Service!", result.getMessage());
  }

  @Test
  public void testSuccessWithMessageAndData() {
    Map<String, String> map = new HashMap<>();
    map.put("test", "test");
    Result<Map<String, String>> result = Result.success("test", map);
    assertEquals("test", result.getData().get("test"));
  }

  @Test
  public void testSuccessWithData() {
    Map<String, String> map = new HashMap<>();
    map.put("test", "test");
    Result<Map<String, String>> result = Result.success(map);
    assertEquals("test", result.getData().get("test"));
  }

  @Test
  public void testSuccessAlone() {
    Result<Object> result = Result.success();
    assertEquals(result.getMessage(), "success");
  }

  @Test
  public void testSetResult() {
    Result<Object> result = Result.success();
    result.setCode(200);
    result.setMessage("success");
    result.setData(null);
  }

  @Test
  public void testResultCode() {
    ResultCode code1 = ResultCode.SUCCESS;
    assertEquals(200, code1.getCode());
    assertEquals("Success", code1.getMsg());
    ResultCode code2 = ResultCode.ERROR;
    assertEquals(500, code2.getCode());
    assertEquals("Internal Service Error", code2.getMsg());
    ResultCode code3 = ResultCode.BAD_REQUEST;
    assertEquals(400, code3.getCode());
    assertEquals("Bad Request", code3.getMsg());
    ResultCode code4 = ResultCode.NOT_FOUND;
    assertEquals(404, code4.getCode());
    assertEquals("Not Found", code4.getMsg());
  }

  @Test
  public void testError() {
    Result<Object> result = Result.error("Test");
    assertEquals("Test", result.getMessage());
  }

}
