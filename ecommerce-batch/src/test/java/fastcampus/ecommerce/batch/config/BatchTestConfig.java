package fastcampus.ecommerce.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class BatchTestConfig {

    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils(@Autowired(required = false) Job productUploadJob) {
        JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
        if (productUploadJob != null) {
            jobLauncherTestUtils.setJob(productUploadJob);
        }
        return jobLauncherTestUtils;
    }
}
