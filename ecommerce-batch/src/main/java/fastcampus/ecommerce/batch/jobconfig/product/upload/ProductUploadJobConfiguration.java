package fastcampus.ecommerce.batch.jobconfig.product.upload;

import fastcampus.ecommerce.batch.domain.product.Product;
import fastcampus.ecommerce.batch.dto.ProductUploadCsvRow;
import fastcampus.ecommerce.batch.service.file.SplitFilePartitioner;
import fastcampus.ecommerce.batch.util.FileUtils;
import fastcampus.ecommerce.batch.util.ReflectionUtils;
import java.io.File;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
public class ProductUploadJobConfiguration {

  @Bean
  public Job productUploadJob(
      JobRepository jobRepository, Step productUploadPartitionStep, JobExecutionListener listener) {
    return new JobBuilder("productUploadJob", jobRepository)
        .listener(listener)
        .start(productUploadPartitionStep)
        .build();
  }

  @Bean
  public Step productUploadPartitionStep(
      JobRepository jobRepository,
      Step productUploadStep,
      SplitFilePartitioner splitFilePartitioner,
      PartitionHandler partitionHandler) {
    return new StepBuilder("productUploadPartitionStep", jobRepository)
        .partitioner(productUploadStep.getName(), splitFilePartitioner)
        .partitionHandler(partitionHandler)
        .allowStartIfComplete(true)
        .build();
  }

  @Bean
  @JobScope
  public SplitFilePartitioner splitFilePartitioner(
      @Value("#{jobParameters['inputFilePath']}") String path,
      @Value("#{jobParameters['gridSize']}") int gridSize) {
    return new SplitFilePartitioner(FileUtils.splitCsv(new File(path), gridSize));
  }

  @Bean
  @JobScope
  public TaskExecutorPartitionHandler partitionHandler(
      TaskExecutor taskExecutor,
      Step productUploadStep,
      @Value("#{jobParameters['gridSize']}") int gridSize) {

    TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
    handler.setTaskExecutor(taskExecutor);
    handler.setStep(productUploadStep);
    handler.setGridSize(gridSize);
    return handler;
  }

  // 잡을 통해서 로컬 상품 csv를 읽어오고 db에 상품 데이터를 넣을수 있음. leader, processor, writer, 변환하는거는 구현체에서 할거임.
  @Bean
  public Step productUploadStep(
      JobRepository jobRepository,
      DataSourceTransactionManager transactionManager,
      StepExecutionListener stepExecutionListener,
      ItemReader<ProductUploadCsvRow> productReader,
      ItemProcessor<ProductUploadCsvRow, Product> productProcessor,
      ItemWriter<Product> productWriter,
      TaskExecutor taskExecutor) {
    return new StepBuilder("productUploadStep", jobRepository)
        .<ProductUploadCsvRow, Product>chunk(1000, transactionManager)
        .reader(productReader)
        .processor(productProcessor)
        .writer(productWriter)
        .allowStartIfComplete(true)
        .listener(stepExecutionListener)
        .taskExecutor(taskExecutor)
        .build();
  }

  @Bean
  @StepScope
  public SynchronizedItemStreamReader<ProductUploadCsvRow> productReader(
      @Value("#{stepExecutionContext['file']}") File file) {
    FlatFileItemReader<ProductUploadCsvRow> reader =
        new FlatFileItemReaderBuilder<ProductUploadCsvRow>()
            .name("productReader")
            .resource(new FileSystemResource(file))
            .delimited()
            .names(ReflectionUtils.getFieldNames(ProductUploadCsvRow.class).toArray(String[]::new))
            .targetType(ProductUploadCsvRow.class)
            .linesToSkip(1)
            .saveState(false) // 멀티스레드에서는 saveState를 false로
            .build();

    return new SynchronizedItemStreamReaderBuilder<ProductUploadCsvRow>().delegate(reader).build();
  }

  @Bean
  public ItemProcessor<ProductUploadCsvRow, Product> productProcessor() {
    return row -> Product.from(row);
  }

  @Bean
  public JdbcBatchItemWriter<Product> productWriter(DataSource dataSource) {
    String sql =
        "insert into products(product_id, seller_id, category, product_name, "
            + "sales_start_date, sales_end_date, product_status, brand, manufacturer, sales_price,"
            + "stock_quantity, created_at, updated_at) values(:productId, :sellerId, :category, :productName, "
            + ":salesStartDate, :salesEndDate, :productStatus, :brand, :manufacturer, :salesPrice, :stockQuantity, :createdAt, :updatedAt)";
    return new JdbcBatchItemWriterBuilder<Product>()
        .dataSource(dataSource)
        .sql(sql)
        .beanMapped()
        .build();
  }
}
