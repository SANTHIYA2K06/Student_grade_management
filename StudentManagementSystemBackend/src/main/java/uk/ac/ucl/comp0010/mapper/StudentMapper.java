package uk.ac.ucl.comp0010.mapper;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import uk.ac.ucl.comp0010.entity.Student;
import uk.ac.ucl.comp0010.vo.StudentListVo;

/**
 * <p>
 * Student Mapper Interface.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-10-17
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {

  IPage<StudentListVo> listStudents(IPage<StudentListVo> ipage,
      @Param(Constants.WRAPPER) QueryWrapper<StudentListVo> wrapper);

  List<String> listPrograms();

}
