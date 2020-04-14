package nd.sched.job.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.IJobExecutor;
import nd.sched.job.JobExecutor;
import nd.sched.job.IJobExecutor.JobReturn;
import nd.sched.job.IJobExecutor.JobStatus;

public class JobFactoryTest {
    private static final Logger logger = LoggerFactory.getLogger(JobFactoryTest.class);
    private JobFactory jobFactory = new JobFactory();

    @BeforeEach
    public void init(){
        final JobExecutor je = new JobExecutor();
        je.setName("Sample");
        jobFactory.registerJobExecutor("Sample", je);
    }
    
    @Test
    public void executeTest(){
        final IJobExecutor je = jobFactory.getJobExecutor("Sample");
        final JobReturn jr = je.execute("Arguments1");
        logger.info("Job Return: {}", jr.returnValue);
        assertEquals(jr.jobStatus, JobStatus.SUCCESS);
    }

}
/*

*/