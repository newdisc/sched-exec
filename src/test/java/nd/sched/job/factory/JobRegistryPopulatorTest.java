package nd.sched.job.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.IJobExecutor;
import nd.sched.job.IJobExecutor.JobReturn;

public class JobRegistryPopulatorTest {
    private static final Logger logger = LoggerFactory.getLogger(JobRegistryPopulatorTest.class);
    private final JobFactory jobFactory = new JobFactory();
    private final IJobRegistryPopulator jobRegistryPopulator = (new JobRegistryPopulator()).setFactory(jobFactory);
    @Test
    public void registeredSampleTest(){
        final Path currentRelativePath = Paths.get("");
        final String pwd = currentRelativePath.toAbsolutePath().toString();        
        logger.info("CWD: {}", pwd);
        jobRegistryPopulator.registerJobs();
        final IJobExecutor je = jobFactory.getJobExecutor("EchoJob");
        final JobReturn jr = je.execute("Arguments JobRegistryPopulatorTest");
        logger.info("Job Returned: {}", jr.getReturnValue());
        assertEquals(IJobExecutor.JobStatus.SUCCESS, jr.getJobStatus());
        //jobRegistryPopulator.printRegistry();
    }
}