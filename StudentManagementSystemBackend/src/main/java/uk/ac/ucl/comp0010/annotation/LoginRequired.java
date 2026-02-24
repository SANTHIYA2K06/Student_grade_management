package uk.ac.ucl.comp0010.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import uk.ac.ucl.comp0010.enums.AccountType;

/**
 * Annotation For Login Required API.
 *
 * @author Jack Pan
 * @since 2024-10-20
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface LoginRequired {

  /**
   * Roles or permissions that are required for accessing the resource.
   *
   * @return an array of required roles or permissions
   */
  AccountType[] accountTypes();

}
