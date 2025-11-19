package fastcampus.ecommerce.batch.jobconfig.product;

import fastcampus.ecommerce.batch.BatchApplication;
import fastcampus.ecommerce.batch.config.BatchTestConfig;
import javax.sql.DataSource;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@Sql(scripts = "/sql/schema.sql")
@SpringBootTest
@AutoConfigureObservability
@SpringJUnitConfig(classes = {BatchApplication.class})
@Import(BatchTestConfig.class)
public abstract class BaseBatchIntegrationTest {
  @Autowired protected JobLauncherTestUtils jobLauncherTestUtils;

  protected JdbcTemplate jdbcTemplate;

  @Autowired
  public void setDataSource(DataSource dataSource) {
    jdbcTemplate = new JdbcTemplate(dataSource);
  }
}
