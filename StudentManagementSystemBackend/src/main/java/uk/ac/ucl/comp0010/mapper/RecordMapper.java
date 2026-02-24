package uk.ac.ucl.comp0010.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import uk.ac.ucl.comp0010.entity.Record;
import uk.ac.ucl.comp0010.vo.RecordDetailVo;
import uk.ac.ucl.comp0010.vo.RecordListVo;

/**
 * <p>
 * Mapper Table Interface for record.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-11-22
 */
public interface RecordMapper extends BaseMapper<Record> {

  IPage<RecordListVo> list(IPage<RecordListVo> ipage,
      @Param(Constants.WRAPPER) QueryWrapper<RecordListVo> wrapper);

  RecordDetailVo get(Long id);

}