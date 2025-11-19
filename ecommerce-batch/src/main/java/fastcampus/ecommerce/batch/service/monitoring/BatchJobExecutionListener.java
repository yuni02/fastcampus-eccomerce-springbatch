package fastcampus.ecommerce.batch.service.monitoring;

import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
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
    log.info("listener: before Job - {}", jobExecution.getJobInstance().getJobName());
  }

  @Override
  public void afterJob(JobExecution jobExecution) { // 잡 시작 후
    String jobName = jobExecution.getJobInstance().getJobName();
    BatchStatus status = jobExecution.getStatus();

    log.info("listener: after Job - {} with status {}", jobName, status);

    // 실행 횟수 증가
    manager.getJobExecutionCounter()
        .labels(jobName, status.toString())
        .inc();

    // 실행 시간 기록
    if (jobExecution.getStartTime() != null && jobExecution.getEndTime() != null) {
      long durationSeconds = Duration.between(
          jobExecution.getStartTime(),
          jobExecution.getEndTime()
      ).getSeconds();

      manager.getJobDurationGauge()
          .labels(jobName)
          .set(durationSeconds);
    }

    // 실패 횟수 기록
    if (status == BatchStatus.FAILED) {
      manager.getJobFailureCounter()
          .labels(jobName)
          .inc();
    }

    // PushGateway로 메트릭 전송
    manager.pushMetrics(Map.of("job_name", jobName));
  }
}
