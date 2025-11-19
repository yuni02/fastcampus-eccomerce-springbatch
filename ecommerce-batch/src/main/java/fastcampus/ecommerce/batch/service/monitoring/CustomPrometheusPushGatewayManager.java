package fastcampus.ecommerce.batch.service.monitoring;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.PushGateway;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomPrometheusPushGatewayManager {

  private final PushGateway pushGateway;
  private final CollectorRegistry registry;
  private final String job;

  public CustomPrometheusPushGatewayManager(
      PushGateway pushGateway,
      CollectorRegistry registry,
      @Value("${prometheus.job.name:spring-batch}") String job) {
    this.pushGateway = pushGateway;
    this.registry = registry;
    this.job = job;
  }

  public void pushMetrics(Map<String, String> groupingKey) {
    try {
      pushGateway.pushAdd(registry, job, groupingKey);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
