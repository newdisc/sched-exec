package nd.sched.job.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.IJobExecutor;
import nd.sched.job.IJobExecutor.JobReturn;
import nd.sched.job.factory.IJobFactory;
import nd.sched.job.factory.JobFactory;

public class ExecutorServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorServiceTest.class);

    @Test
    public void execSvcTest(){
        ExecutorService execSvc = new ExecutorService();
        IJobFactory jobFactory = new JobFactory();
        execSvc.setJobFactory(jobFactory);
        execSvc.load();
        final IJobExecutor je = jobFactory.getJobExecutor("Sample");
        final JobReturn jr = je.execute("Arguments JobRegistryPopulatorTest");
        logger.info("Job Returned: {}", jr.returnValue);
        assertEquals(jr.jobStatus, IJobExecutor.JobStatus.SUCCESS);
        execSvc.jobRegistryPopulators.forEach(pop -> pop.printRegistry());        
    }

    @Test
    public void executeTest(){
        ExecutorService execSvc = new ExecutorService();
        IJobFactory jobFactory = new JobFactory();
        execSvc.setJobFactory(jobFactory);
        execSvc.load();
        final JobReturn jr = execSvc.execute("Sample", "Arguments");
        logger.info("Job Returned: {}", jr.returnValue);
        assertEquals(jr.jobStatus, IJobExecutor.JobStatus.SUCCESS);
    }
}