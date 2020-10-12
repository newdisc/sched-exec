package nd.sched.job.factory;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.CommandJobExecutor;
import nd.sched.job.JavaJobExecutor;
import nd.sched.job.JobExecutor;

public class JobRegistryPopulator implements IJobRegistryPopulator{
    private static final Logger logger = LoggerFactory.getLogger(JobRegistryPopulator.class);
    private static final String JOB_FILE = "/jobs.csv";
    private static final String JAVA_JOB_FILE = "/java.jobs.csv";
    private final IJobFactory jobFactory;
    private final String base;
    public JobRegistryPopulator(IJobFactory jf, final String baseDir){
        jobFactory = jf;
        base = baseDir;
    }
    @Override
    public void registerJobs() {
        final JobExecutor je = new JobExecutor();
        je.setName("Sample");
        jobFactory.registerJobExecutor("Sample", je);
        logger.info("Registered Sample job");
        List<CommandJobExecutor> jobs = JobRegistryPopulator.createBeans(base + JOB_FILE, CommandJobExecutor.class);
        List<JavaJobExecutor> javaJobs = JobRegistryPopulator.createBeans(base + JAVA_JOB_FILE, JavaJobExecutor.class);
        jobs.forEach(exec -> {
        	jobFactory.registerJobExecutor(exec.getName(), exec);
        	logger.info("Registered job: {}", exec.getName());
        });
        javaJobs.forEach(jj -> {
        	jobFactory.registerJobExecutor(jj.getName(), jj);
            logger.info("Registered Java job: {}", jj.getName());
        });
    }
    public static <T> List<T> createBeans(final String filename, Class<T> typeClass) {
        HeaderColumnNameMappingStrategy<T> hcnms = new HeaderColumnNameMappingStrategy<>();
        hcnms.setType(typeClass);
        try (final Reader reader = new FileReader(filename); ) {
            return new CsvToBeanBuilder<T>(reader)
                .withMappingStrategy(hcnms)
                .build()
                .parse();
        } catch (IOException e) {
            final String msg = "Issue reading Job File";
            logger.error(msg, e);
        }
        return new ArrayList<>();
    }
    @Override
    public void printRegistry(){
        logger.info("Registered Jobs: ");
        jobFactory.printJobsRegistered();
    }
}