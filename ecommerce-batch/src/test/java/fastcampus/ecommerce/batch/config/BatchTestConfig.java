package fastcampus.ecommerce.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class BatchTestConfig {

    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils(
            JobLauncher jobLauncher,
            JobRepository jobRepository,
            @Autowired(required = false) Job productUploadJob) {
        JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
        jobLauncherTestUtils.setJobLauncher(jobLauncher);
        jobLauncherTestUtils.setJobRepository(jobRepository);
        if (productUploadJob != null) {
            jobLauncherTestUtils.setJob(productUploadJob);
        }
        return jobLauncherTestUtils;
    }
}
