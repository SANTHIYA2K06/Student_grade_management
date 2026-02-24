package uk.ac.ucl.comp0010.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ucl.comp0010.dto.CreateStudentDto;
import uk.ac.ucl.comp0010.dto.EditStudentDto;
import uk.ac.ucl.comp0010.dto.LoginDto;
import uk.ac.ucl.comp0010.dto.ResetPasswordDto;
import uk.ac.ucl.comp0010.entity.Student;
import uk.ac.ucl.comp0010.response.Result;
import uk.ac.ucl.comp0010.vo.LoginVo;
import uk.ac.ucl.comp0010.vo.StudentListVo;
import uk.ac.ucl.comp0010.vo.StudentRegistrationListVo;
import uk.ac.ucl.comp0010.vo.StudentVo;

/**
 * <p>
 * Student Service Interface.
 * </p>
 *
 * @author Jack Pan
 * @since 2024-10-17
 */

public interface StudentService extends IService<Student> {

  Result<LoginVo> login(LoginDto loginDto);

  Result<Object> addStudent(CreateStudentDto createStudentDto);

  Result<IPage<StudentListVo>> listStudents(Integer current, Integer size, Integer id,
      String fullName, String username, String programOfStudy);

  Result<Object> importStudent(MultipartFile file);

  Result<Object> edit(EditStudentDto editStudentDto, Integer id);

  Result<Object> delete(Integer id);

  void resetPassword(Integer accountId, ResetPasswordDto resetPasswordDto);

  Result<StudentVo> detail();

  Result<StudentVo> getStudentDetail(Integer id);

  Result<IPage<StudentRegistrationListVo>> listStudentRegistrations(Integer current, Integer size);

  Result<List<String>> listPrograms();
}
