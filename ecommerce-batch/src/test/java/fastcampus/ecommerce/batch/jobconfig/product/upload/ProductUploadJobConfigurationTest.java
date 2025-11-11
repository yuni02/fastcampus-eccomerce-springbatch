package fastcampus.ecommerce.batch.jobconfig.product.upload;

import fastcampus.ecommerce.batch.BatchApplication;
import javax.sql.DataSource;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@Sql("/sql/schema.sql")
@SpringBatchTest
@SpringJUnitConfig(classes = BatchApplication.class)
@TestPropertySource(properties = "spring.batch.job.names=productUploadJob")
class ProductUploadJobConfigurationTest {
  @Autowired private JobLauncherTestUtils jobLauncherTestUtils;

  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setDataSource(DataSource dataSource) {
    jdbcTemplate = new JdbcTemplate(dataSource);
  }
}
