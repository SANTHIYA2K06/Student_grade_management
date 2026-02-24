package uk.ac.ucl.comp0010.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ucl.comp0010.dto.CreateRegistrationDto;
import uk.ac.ucl.comp0010.dto.EditRegistrationDto;
import uk.ac.ucl.comp0010.entity.Registration;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.vo.RegistrationListVo;


/**
 * <p>
 * Service Interface for registration.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-10-18
 */
public interface RegistrationService extends IService<Registration> {

  Result<Object> addRegistration(CreateRegistrationDto createRegistrationDto);

  Result<Object> editRegistration(EditRegistrationDto editRegistrationDto, Long id);

  Result<IPage<RegistrationListVo>> list(Integer current, Integer size, Integer studentId,
      Long recordId);

  Result<Object> delete(Long id);

  Result<Object> importRegistrationByCsv(MultipartFile file);

}
