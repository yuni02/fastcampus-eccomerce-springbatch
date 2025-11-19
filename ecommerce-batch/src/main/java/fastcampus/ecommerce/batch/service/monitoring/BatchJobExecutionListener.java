package fastcampus.ecommerce.batch.service.monitoring;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

// 공통으로 써도 됨.
@Slf4j
@Component
@RequiredArgsConstructor
public class BatchJobExecutionListener implements JobExecutionListener {

  private final CustomPrometheusPushGatewayManager manager;

  @Override
  public void beforeJob(JobExecution jobExecution) { // 잡 시작 전
    log.info("listener: before Job");
  }

  @Override
  public void afterJob(JobExecution jobExecution) { // 잡 시작 후
    log.info("listener: after Job");
    manager.pushMetrics(Map.of("job_name", jobExecution.getJobInstance().getJobName()));
  }
}
