package com.phoenix_sat.phoenix_sat_backend.batch;

import com.phoenix_sat.phoenix_sat_backend.entity.User;
import com.phoenix_sat.phoenix_sat_backend.model.request.UserRequest;
import com.phoenix_sat.phoenix_sat_backend.repository.OrganizationRepository;
import com.phoenix_sat.phoenix_sat_backend.repository.RoleRepository;
import com.phoenix_sat.phoenix_sat_backend.repository.UserRepository;
import com.phoenix_sat.phoenix_sat_backend.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.FileInputStream;

@Configuration
@RequiredArgsConstructor
public class UserImportJobConfig {
    private final FileService fileService;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;

    @Bean
    @StepScope
    @SneakyThrows
    public FlatFileItemReader<UserRequest> userRequestFlatFileItemReader(@Value("#{jobParameters[filename]}") String filename,
                                                                         @Value("#{jobParameters[organizationId]}") String organizationId) {
        FlatFileItemReader<UserRequest> reader = new FlatFileItemReader<>();
        reader.setResource(new InputStreamResource(new FileInputStream(fileService.getFile(filename))));
        reader.setName("User-CSV-Reader");
        reader.setLinesToSkip(1);
        reader.setLineMapper(lineMapper(organizationId));
        return reader;
    }

    private LineMapper<UserRequest> lineMapper(String organizationId) {
        DefaultLineMapper<UserRequest> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("name", "email", "role");

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(new UserFieldMapper(organizationId));

        return lineMapper;
    }

    @Bean("userCsvImportStep")
    @JobScope
    public Step importUserCsvStep(FlatFileItemReader<UserRequest> reader,
                                  @Qualifier("userItemProcessor") ItemProcessor<UserRequest, User> processor,
                                  @Qualifier("userItemWriter") UserItemWriter writer) {
        return new StepBuilder("userCsvImportStep", jobRepository)
                .<UserRequest, User>chunk(10, platformTransactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean("importUsersJob")
    public Job runJob(@Qualifier("userCsvImportStep") Step step) {
        return new JobBuilder("importUserItems", jobRepository).start(step).build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(10);
        return taskExecutor;
    }

    @Bean
    @StepScope
    public UserItemWriter userItemWriter() {
        return new UserItemWriter(this.userRepository, this.organizationRepository);
    }

    @Bean("userItemProcessor")
    @StepScope
    public ItemProcessor<UserRequest, User> userItemProcessor() {
        return new UserItemProcessor(roleRepository);
    }
}
