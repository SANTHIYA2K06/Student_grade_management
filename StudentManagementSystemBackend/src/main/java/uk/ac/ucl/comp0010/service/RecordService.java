package uk.ac.ucl.comp0010.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ucl.comp0010.dto.CreateRecordDto;
import uk.ac.ucl.comp0010.dto.EditRecordDto;
import uk.ac.ucl.comp0010.entity.Record;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.vo.RecordDetailVo;
import uk.ac.ucl.comp0010.vo.RecordListVo;


/**
 * <p>
 * Service Interface for record.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-11-22
 */
public interface RecordService extends IService<Record> {

  Result<Map<String, Long>> create(CreateRecordDto createRecordDto);

  Result<Object> edit(EditRecordDto editRecordDto, Long id);

  Result<IPage<RecordListVo>> list(Integer current, Integer size,
      Integer year, Integer month, Integer day, String moduleCode);

  Result<Object> delete(Long id);

  Result<RecordDetailVo> get(Long id);

  Result<Object> importRecordByCsv(MultipartFile file);

}
