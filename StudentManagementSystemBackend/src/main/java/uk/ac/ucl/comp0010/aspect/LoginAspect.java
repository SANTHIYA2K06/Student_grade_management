package uk.ac.ucl.comp0010.aspect;

import jakarta.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.ac.ucl.comp0010.annotation.LoginRequired;
import uk.ac.ucl.comp0010.enums.AccountType;
import uk.ac.ucl.comp0010.enums.JwtType;
import uk.ac.ucl.comp0010.exception.NoAccessException;
import uk.ac.ucl.comp0010.exception.ServerException;
import uk.ac.ucl.comp0010.exception.UnauthorizedException;
import uk.ac.ucl.comp0010.model.LoginEntity;
import uk.ac.ucl.comp0010.model.Payload;
import uk.ac.ucl.comp0010.service.AdminService;
import uk.ac.ucl.comp0010.service.StaffService;
import uk.ac.ucl.comp0010.service.StudentService;
import uk.ac.ucl.comp0010.utils.JwtUtils;


/**
 * Login Required Checking.
 *
 * @author Jack Pan
 * @since 2024-10-19
 */
@Aspect
@Component
public class LoginAspect {

  public static final ThreadLocal<LoginEntity> threadLocal = new ThreadLocal<>();

  @Resource
  private StudentService studentService;

  @Resource
  private StaffService staffService;

  @Resource
  private AdminService adminService;


  @Pointcut("@annotation(uk.ac.ucl.comp0010.annotation.LoginRequired)")
  public void loginRequired() {
  }

  /**
   * Check for Login and set Login Entities.
   *
   * @author Jack Pan
   * @since 2024-10-20
   */
  @Around("loginRequired()")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    // Getting Access Token from header
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
        .getRequestAttributes();
    String accessToken = null;
    if (attributes != null) {
      accessToken = attributes.getRequest().getHeader("Authorization");
    }
    // Decode Access Token
    Payload payload;
    try {
      payload = JwtUtils.getPayloadFromJwt(accessToken);
    } catch (ServerException e) {
      throw new UnauthorizedException(e.getMessage());
    }

    // Check Access Token
    if (payload.getJwtType() != JwtType.access_token) {
      throw new UnauthorizedException("Please access Resources using accessToken");
    }
    // Check Permissions
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    LoginRequired annotation = method.getAnnotation(LoginRequired.class);
    List<AccountType> requiredRoles = Arrays.asList(annotation.accountTypes());
    if (!requiredRoles.contains(payload.getAccountType())) {
      throw new NoAccessException("No access To the Resources");
    }
    if (payload.getAccountType() == AccountType.student) {
      if (studentService.getById(payload.getAccountId()) == null) {
        throw new UnauthorizedException("No such student");
      }
    } else if (payload.getAccountType() == AccountType.staff) {
      if (staffService.getById(payload.getAccountId()) == null) {
        throw new UnauthorizedException("No such staff");
      }
    } else if (payload.getAccountType() == AccountType.admin) {
      if (adminService.getById(payload.getAccountId()) == null) {
        throw new UnauthorizedException("No such admin");
      }
    }
    // Setting Attributes
    LoginEntity loginEntity = new LoginEntity();
    loginEntity.setId(payload.getAccountId());
    loginEntity.setAccountType(payload.getAccountType()); // set accountType
    threadLocal.set(loginEntity);
    return joinPoint.proceed();
  }

  @After("loginRequired()")
  public void afterLoginRequiredMethods() {
    threadLocal.remove();
  }
}

