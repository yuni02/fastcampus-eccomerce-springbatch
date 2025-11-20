package fastcampus.ecommerce.batch.service.monitoring;

import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchStepExecutionListener implements StepExecutionListener, ChunkListener {
  private final CustomPrometheusPushGatewayManager manager;

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    String jobName = stepExecution.getJobExecution().getJobInstance().getJobName();
    String stepName = stepExecution.getStepName();
    BatchStatus status = stepExecution.getStatus();

    log.info("listener: after step - {} in job {} with status {}", stepName, jobName, status);
    log.info("listener: step execution context: {}", stepExecution.getExecutionContext());

    // Step 실행 횟수 증가
    manager.getStepExecutionCounter().labels(jobName, stepName, status.toString()).inc();

    // Step 실행 시간 기록
    if (stepExecution.getStartTime() != null && stepExecution.getEndTime() != null) {
      long durationSeconds =
          Duration.between(stepExecution.getStartTime(), stepExecution.getEndTime()).getSeconds();

      manager.getStepDurationGauge().labels(jobName, stepName).set(durationSeconds);
    }

    // Step 실패 횟수 기록
    if (status == BatchStatus.FAILED) {
      manager.getStepFailureCounter().labels(jobName, stepName).inc();
    }

    // Note: Job 레벨에서 모든 메트릭을 한번에 push하므로 여기서는 push하지 않음

    return ExitStatus.COMPLETED;
  }

  @Override
  public void afterChunk(ChunkContext context) {
    manager.pushMetrics(
        Map.of(
            "job_name",
            context
                .getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getJobInstance()
                .getJobName()));
    ChunkListener.super.afterChunk(context);
  }
}
