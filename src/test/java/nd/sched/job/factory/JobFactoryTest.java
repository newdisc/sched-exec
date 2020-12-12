package nd.sched.job.factory;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.BaseJobExecutor;
import nd.sched.job.JobReturn;
import nd.sched.job.JobReturn.JobStatus;

class JobFactoryTest {
    private static final Logger logger = LoggerFactory.getLogger(JobFactoryTest.class);
    private JobFactory jobFactory = new JobFactory();

    @BeforeEach
    void init(){
        final BaseJobExecutor je = new BaseJobExecutor();
        je.setName("Sample");
        jobFactory.registerJobExecutor("Sample", je);
    }
    
    @Test
    void executeTest(){
        final BaseJobExecutor je = jobFactory.getJobExecutor("Sample");
        je.executeAsync("Arguments1", j -> {
            JobReturn jr = j; 
            logger.info("Job Return: {}", jr.getReturnValue());
            assertEquals(JobStatus.SUCCESS, jr.getJobStatus());
        	return j;
        });
    }

}
/*

*/