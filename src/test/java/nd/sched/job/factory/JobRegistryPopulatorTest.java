package nd.sched.job.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.BaseJobExecutor;
import nd.sched.job.JobReturn;
import nd.sched.job.JobReturn.JobStatus;

class JobRegistryPopulatorTest {
    private static final Logger logger = LoggerFactory.getLogger(JobRegistryPopulatorTest.class);
    private final JobFactory jobFactory = new JobFactory();
    private final JobRegistryPopulator jobRegistryPopulator = (new JobRegistryPopulator()).setFactory(jobFactory);
    @Test
    void registeredSampleTest(){
        final Path currentRelativePath = Paths.get("");
        final String pwd = currentRelativePath.toAbsolutePath().toString();        
        logger.info("CWD: {}", pwd);
        jobRegistryPopulator.registerJobs();
        final BaseJobExecutor je = jobFactory.getJobExecutor("EchoJob");
        je.executeAsync("Arguments JobRegistryPopulatorTest", j -> {
            JobReturn jr = j; 
            logger.info("Job Return: {}", jr.getReturnValue());
            assertEquals(JobStatus.SUCCESS, jr.getJobStatus());
        	return j;
        });
    }
}