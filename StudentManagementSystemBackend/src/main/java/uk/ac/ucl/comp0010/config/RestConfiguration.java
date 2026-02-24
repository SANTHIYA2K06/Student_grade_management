package uk.ac.ucl.comp0010.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import uk.ac.ucl.comp0010.entity.Admin;
import uk.ac.ucl.comp0010.entity.Module;
import uk.ac.ucl.comp0010.entity.Record;
import uk.ac.ucl.comp0010.entity.Registration;
import uk.ac.ucl.comp0010.entity.Staff;
import uk.ac.ucl.comp0010.entity.Student;


/**
 * Rest Configuration.
 *
 * @author Jack Pan
 * @since 2024-12-01
 */

@Configuration
public class RestConfiguration implements RepositoryRestConfigurer {

  @Override
  public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config,
      CorsRegistry cors) {
    config.exposeIdsFor(Student.class);
    config.exposeIdsFor(Module.class);
    config.exposeIdsFor(Staff.class);
    config.exposeIdsFor(Record.class);
    config.exposeIdsFor(Registration.class);
    config.exposeIdsFor(Admin.class);
  }

}
