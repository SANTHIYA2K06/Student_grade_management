package uk.ac.ucl.comp0010.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Password Encoder.
 *
 * @author Jack Pan
 * @since 2024-10-20
 */
public class PasswordEncoderUtils {

  public static String encode(String plainPassword) {
    return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
  }

  /**
   * Check if the password is correct.
   *
   * @param plainPassword  raw password
   * @param hashedPassword hashed password
   * @return True if password is correct, False if password is incorrect.
   */

  public static boolean checkPassword(String plainPassword, String hashedPassword) {
    return BCrypt.checkpw(plainPassword, hashedPassword);
  }

}
