package uk.ac.ucl.comp0010.test;

import org.springframework.mock.web.MockMultipartFile;

/**
 * Utilities that will use in the test.
 *
 * @author Jack Pan
 * @since 2024-12-03
 */

public class TestUtils {

  /**
   * Generate a csv file for test.
   *
   * @param content Content of the file
   * @return csv file
   */

  public static MockMultipartFile getCsvMockMultipartFile(String content) {
    return new MockMultipartFile(
        "file",               // form-data params name
        "students.csv",       // file name
        "text/csv",           // file type
        content.getBytes() // file content
    );
  }

}
