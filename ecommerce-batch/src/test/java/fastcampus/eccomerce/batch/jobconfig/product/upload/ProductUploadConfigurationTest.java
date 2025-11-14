package fastcampus.eccomerce.batch.jobconfig.product.upload;

import fastcampus.eccomerce.batch.jobconfig.BaseBatchIntegrationTest;
import fastcampus.ecommerce.batch.BatchApplication;
import fastcampus.ecommerce.batch.config.BatchTestConfig;
import fastcampus.ecommerce.batch.service.product.ProductService;
import java.io.IOException;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.core.io.Resource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestPropertySource(properties = {
    "spring.batch.job.enabled=false"
})
public class ProductUploadConfigurationTest extends BaseBatchIntegrationTest {



    @Autowired
    private ProductService productService;

    @Value("classpath:/data/products_for_upload.csv")
    private Resource input;

    @Test
    void testJob(@Autowired Job productUploadJob) throws Exception {
        JobParameters jobParameters = jobParameters();
        jobLauncherTestUtils.setJob(productUploadJob);
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        assertAll(() -> assertThat(productService.countProducts()).isEqualTo(6),
            () -> assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode()));

    }


    private JobParameters jobParameters() throws IOException {
        return new JobParametersBuilder().addJobParameter("inputFilePath",
            new JobParameter<>(input.getFile().getPath(), String.class, false)).toJobParameters();
    }
}
