package fastcampus.ecommerce.batch.jobconfig.product.upload;

import fastcampus.ecommerce.batch.dto.ProductUploadCsvRow;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

@Configuration
public class ProductUploadJobConfiguration {

    @Bean
    public Job productUploadJob(JobRepository jobRepository, Step productUploadStep,
        JobExecutionListener listener) {
        return new JobBuilder("", jobRepository).start(productUploadStep).build();
    }

    @Bean
    public Step productUploadStep(JobRepository jobRepository,
        DataSourceTransactionManager transactionManager,
        StepExecutionListener stepExecutionListener) {
        return new StepBuilder("productUploadStep",
            jobRepository).<ProductUploadCsvRow, ProductUploadCsvRow>chunk(1000, transactionManager)
            .allowStartIfComplete(true)// 개발단게에서만 true넣고 운영에서는 false하는게
            .listener(stepExecutionListener)// 나음
            .build();
    }

}
