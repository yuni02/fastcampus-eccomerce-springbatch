package fastcampus.ecommerce.batch.service.monitoring;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomPrometheusPushGatewayManager {

  private final PushGateway pushGateway;
  private final CollectorRegistry registry;
  private final String job;

  @Getter
  private final Counter jobExecutionCounter;
  @Getter
  private final Counter stepExecutionCounter;
  @Getter
  private final Gauge jobDurationGauge;
  @Getter
  private final Gauge stepDurationGauge;
  @Getter
  private final Counter jobFailureCounter;
  @Getter
  private final Counter stepFailureCounter;

  public CustomPrometheusPushGatewayManager(
      PushGateway pushGateway,
      CollectorRegistry registry,
      @Value("${prometheus.job.name:spring-batch}") String job) {
    this.pushGateway = pushGateway;
    this.registry = registry;
    this.job = job;

    // Job 메트릭 초기화
    this.jobExecutionCounter = Counter.build()
        .name("spring_batch_job_executions_total")
        .help("Total number of job executions")
        .labelNames("job_name", "status")
        .register(registry);

    this.jobDurationGauge = Gauge.build()
        .name("spring_batch_job_duration_seconds")
        .help("Duration of job execution in seconds")
        .labelNames("job_name")
        .register(registry);

    this.jobFailureCounter = Counter.build()
        .name("spring_batch_job_failures_total")
        .help("Total number of job failures")
        .labelNames("job_name")
        .register(registry);

    // Step 메트릭 초기화
    this.stepExecutionCounter = Counter.build()
        .name("spring_batch_step_executions_total")
        .help("Total number of step executions")
        .labelNames("job_name", "step_name", "status")
        .register(registry);

    this.stepDurationGauge = Gauge.build()
        .name("spring_batch_step_duration_seconds")
        .help("Duration of step execution in seconds")
        .labelNames("job_name", "step_name")
        .register(registry);

    this.stepFailureCounter = Counter.build()
        .name("spring_batch_step_failures_total")
        .help("Total number of step failures")
        .labelNames("job_name", "step_name")
        .register(registry);
  }

  public void pushMetrics(Map<String, String> groupingKey) {
    try {
      log.info("Pushing metrics to PushGateway with grouping key: {}", groupingKey);
      pushGateway.pushAdd(registry, job, groupingKey);
      log.info("Successfully pushed metrics to PushGateway");
    } catch (Exception e) {
      log.error("Failed to push metrics to PushGateway: {}", e.getMessage(), e);
    }
  }
}
