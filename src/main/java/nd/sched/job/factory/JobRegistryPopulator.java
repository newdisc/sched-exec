package nd.sched.job.factory;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.CommandJobExecutor;
import nd.sched.job.JobExecutor;

public class JobRegistryPopulator implements IJobRegistryPopulator{
    private static final Logger logger = LoggerFactory.getLogger(JobRegistryPopulator.class);
    private static final String JOB_FILE = "/jobs.csv";
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
        if (null != jobs) {
            jobs.forEach(exec -> jobFactory.registerJobExecutor(exec.getName(), exec));
            jobs.forEach(exec -> {
                logger.info("Registered job: {}", exec.getName());
            });
        }
    }
    public static <T> List<T> createBeans(final String filename, Class<T> typeClass) {
        HeaderColumnNameMappingStrategy<T> hcnms = new HeaderColumnNameMappingStrategy<>();
        hcnms.setType(typeClass);
        try (final Reader reader = new FileReader(filename); ) {
            List<T> jobs = new CsvToBeanBuilder<T>(reader)
                .withMappingStrategy(hcnms)
                .build()
                .parse();
            return jobs;
        } catch (IOException e) {
            final String msg = "Issue reading Job File";
            logger.error(msg, e);
        }
        return null;
    }
    @Override
    public void printRegistry(){
        logger.info("Registered Jobs: ");
        jobFactory.printJobsRegistered();
    }
}