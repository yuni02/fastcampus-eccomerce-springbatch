package fastcampus.ecommerce.batch.jobconfig.product.upload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import fastcampus.ecommerce.batch.jobconfig.product.BaseBatchIntegrationTest;
import fastcampus.ecommerce.batch.service.product.ProductService;
import java.io.IOException;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "spring.batch.job.names=productUploadJob")
class ProductUploadJobConfigurationTest extends BaseBatchIntegrationTest {
  @Autowired private JobLauncherTestUtils jobLauncherTestUtils;

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setDataSource(DataSource dataSource) {
    jdbcTemplate = new JdbcTemplate(dataSource);
  }

  @Value("classpath:/data/products_for_upload.csv")
  private Resource input;

  @Autowired private ProductService productService;

  @Test
  void testJob(@Autowired Job productUploadJob) throws Exception {
    JobParameters jobParameters = jobParameters();
    jobLauncherTestUtils.setJob(productUploadJob);
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
    assertAll(
        () -> assertThat(productService.countProducts()).isEqualTo(6),
        () -> assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode()));
  }

  private JobParameters jobParameters() throws IOException {

    return new JobParametersBuilder()
        .addJobParameter(
            "inputFilePath", new JobParameter<>(input.getFile().getPath(), String.class, false))
        .addJobParameter("gridSize", new JobParameter<>(3, Integer.class, false))
        .toJobParameters();
  }
}
