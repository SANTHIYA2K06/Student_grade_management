package uk.ac.ucl.comp0010.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import uk.ac.ucl.comp0010.dto.CreateModuleDto;
import uk.ac.ucl.comp0010.dto.EditModuleDto;
import uk.ac.ucl.comp0010.entity.Module;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.vo.ModuleDetailVo;
import uk.ac.ucl.comp0010.vo.ModuleListVo;


/**
 * <p>
 * Service Interface for module.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-10-18
 */
public interface ModuleService extends IService<Module> {

  Result<Object> add(CreateModuleDto createModuleDto);

  Result<IPage<ModuleListVo>> list(Integer current, Integer size, String code, String name,
      String leader);

  Result<Object> edit(EditModuleDto editModuleDto, String code);

  Result<Object> delete(String code);

  Result<ModuleDetailVo> get(String code);

}
