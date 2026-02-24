package uk.ac.ucl.comp0010.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Mybatisplus.
 *
 * @author Jack Pan
 * @since 2024-10-25
 */

@Configuration
public class MybatisPlusConfig {

  /**
   * Bean for using features in mybatisplus.
   * <p>
   * It's for using mybatisplus features.
   * </p>
   */

  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
    return interceptor;
  }
}

