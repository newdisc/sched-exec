package nd.sched.job;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.IJobExecutor.JobReturn;

public class JobExecutorTest {
    private static final Logger logger = LoggerFactory.getLogger(JobExecutorTest.class);
    private final JobExecutor jobExecutor = new JobExecutor();
    private final String TEST_NAME = "Name1";
    private final String TEST_ARGS = "Arguments1";
    @Test
    public void nameTest(){
        jobExecutor.setName(TEST_NAME);
        final String name = jobExecutor.getName();
        logger.info("Exector Name Test: {}", name);
        assertEquals(TEST_NAME, name);
    }
    @Test
    public void executeTest(){
        jobExecutor.setName(TEST_NAME);
        final JobReturn ret = jobExecutor.execute(TEST_ARGS);
        assertEquals(IJobExecutor.JobStatus.SUCCESS, ret.jobStatus);
    }
}