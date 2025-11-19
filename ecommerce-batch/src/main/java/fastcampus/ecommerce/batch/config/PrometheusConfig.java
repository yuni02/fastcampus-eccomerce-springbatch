package fastcampus.ecommerce.batch.config;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.PushGateway;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class PrometheusConfig {

  @Bean
  public CollectorRegistry collectorRegistry() {
    return CollectorRegistry.defaultRegistry;
  }

  @Bean
  public PushGateway pushGateway(
      @Value("${prometheus.pushgateway.url}") String pushGatewayUrl)
      throws MalformedURLException {
    log.info("Configuring PushGateway with URL: {}", pushGatewayUrl);
    return new PushGateway(new URL(pushGatewayUrl));
  }
}
