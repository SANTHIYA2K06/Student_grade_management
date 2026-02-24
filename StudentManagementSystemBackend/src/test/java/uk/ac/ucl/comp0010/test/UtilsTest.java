package uk.ac.ucl.comp0010.test;

import org.junit.jupiter.api.Test;
import uk.ac.ucl.comp0010.utils.CsvUtils;
import uk.ac.ucl.comp0010.utils.JwtUtils;
import uk.ac.ucl.comp0010.utils.PasswordEncoderUtils;

/**
 * Test initialize utilities.
 *
 * @author Jack Pan
 * @since 2024-12-03
 */

public class UtilsTest {

  @Test
  public void testCreateUtils() {
    new CsvUtils();
    new JwtUtils();
    new PasswordEncoderUtils();
  }

}
