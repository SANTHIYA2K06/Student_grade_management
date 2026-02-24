package uk.ac.ucl.comp0010.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import uk.ac.ucl.comp0010.entity.Module;
import uk.ac.ucl.comp0010.vo.ModuleDetailVo;
import uk.ac.ucl.comp0010.vo.ModuleListVo;


/**
 * <p>
 * Mapper Table Interface for Module.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-10-18
 */
public interface ModuleMapper extends BaseMapper<Module> {

  IPage<ModuleListVo> list(IPage<ModuleListVo> ipage,
      @Param(Constants.WRAPPER) QueryWrapper<ModuleListVo> wrapper);

  ModuleDetailVo get(String code);

}
