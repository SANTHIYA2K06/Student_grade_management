package uk.ac.ucl.comp0010.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import uk.ac.ucl.comp0010.entity.Staff;
import uk.ac.ucl.comp0010.vo.StaffListVo;


/**
 * <p>
 * Mapper Table Interface for Staff.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-10-18
 */
public interface StaffMapper extends BaseMapper<Staff> {

  IPage<StaffListVo> listStaffs(IPage<StaffListVo> ipage,
      @Param(Constants.WRAPPER) QueryWrapper<StaffListVo> wrapper);

  List<String> listDepartments();

  List<String> listTitles();

}
