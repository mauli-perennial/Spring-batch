package com.example.springbatch.job;

import com.example.springbatch.dto.StudentDto;
import com.example.springbatch.mapper.StudentFileRowMapper;
import com.example.springbatch.model.Student;
import com.example.springbatch.processor.StudentProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import javax.sql.DataSource;

@Configuration
public class StudentDemoJob {

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private StudentProcessor studentProcessor;
    private DataSource dataSource;

    @Autowired
    public StudentDemoJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, StudentProcessor studentProcessor, DataSource dataSource) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
    }

    @Qualifier(value = "student")
    @Bean
    public Job studentJob() throws Exception {
        return this.jobBuilderFactory.get("student")
                .start(studentJobStep())
                .build();
    }

    @Bean
    public Step studentJobStep() throws Exception {
        return this.stepBuilderFactory.get("step1")
                .<StudentDto, Student>chunk(5)
                .reader(studentReader())
                .processor(studentProcessor)
                .writer(studentDBWriterDefault())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    @StepScope
    Resource inputFileResource(@Value("#{jobParameters[fileName]}") final String fileName) throws Exception {
        return new ClassPathResource(fileName);
    }

    @Bean
    @StepScope
    public FlatFileItemReader<StudentDto> studentReader() throws Exception {
        FlatFileItemReader<StudentDto> reader = new FlatFileItemReader<>();
        reader.setResource(inputFileResource(null));
        reader.setLineMapper(new DefaultLineMapper<StudentDto>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames("studentId", "firstName", "lastName", "email", "age");
                setDelimiter(",");
            }});
            setFieldSetMapper(new StudentFileRowMapper());
        }});
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<Student> studentDBWriterDefault() {
        JdbcBatchItemWriter<Student> itemWriter = new JdbcBatchItemWriter<Student>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("insert into student (student_id, first_name, last_name, email, age) values (:studentId, :firstName, :lastName, :email, :age)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Student>());
        return itemWriter;
    }

    @Bean
    public TaskExecutor taskExecutor(){
        SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
        simpleAsyncTaskExecutor.setConcurrencyLimit(5);
        return simpleAsyncTaskExecutor;
    }

}
