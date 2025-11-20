package fastcampus.ecommerce.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
public class BatchApplication {

  public static void main(String[] args) {
    SpringApplication.run(BatchApplication.class, args);
  }

  @Bean
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(128);
    executor.setMaxPoolSize(128);
    executor.setQueueCapacity(128);
    executor.setAllowCoreThreadTimeOut(true); // 기본 스레드를 유휴상태로 안남게 하는 설정
    executor.setAwaitTerminationSeconds(10);
    return executor;
  }
}
