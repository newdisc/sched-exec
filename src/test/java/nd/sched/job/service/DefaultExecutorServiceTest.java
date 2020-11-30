package nd.sched.job.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.IJobExecutor;
import nd.sched.job.JobExecutor;
import nd.sched.job.IJobExecutor.JobReturn;
import nd.sched.job.factory.IJobFactory;
import nd.sched.job.factory.JobFactory;

public class DefaultExecutorServiceTest {
	private static final Logger logger = LoggerFactory.getLogger(DefaultExecutorServiceTest.class);
    @Test
    public void execSvcTest(){
        JobExecutorService execSvc = new DefaultExecutorService();
        IJobFactory jobFactory = new JobFactory();
        execSvc.setJobFactory(jobFactory);
        jobFactory.registerJobExecutor("Sample", new JobExecutor());
        //execSvc.load();
        final IJobExecutor je = jobFactory.getJobExecutor("Sample");
        final JobReturn jr = je.execute("Arguments JobRegistryPopulatorTest");
        logger.info("Job Returned: {}", jr.getReturnValue());
        assertEquals(IJobExecutor.JobStatus.SUCCESS, jr.getJobStatus());
        //execSvc.getJobRegistryPopulators().forEach(pop -> pop.printRegistry());        
    }
}
