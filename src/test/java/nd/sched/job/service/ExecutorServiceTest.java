package nd.sched.job.service;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Properties;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.BaseJobExecutor;
import nd.sched.job.IJobExecutor;
import nd.sched.job.JobReturn;
import nd.sched.job.JobReturn.JobStatus;
import nd.sched.job.factory.IJobFactory;
import nd.sched.job.factory.IJobRegistryPopulator;
import nd.sched.job.factory.JobFactory;
import nd.sched.job.factory.JobRegistryPopulator;

class ExecutorServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorServiceTest.class);
    
    IJobFactory jobFactory;
    JobExecutorService execSvc;

    @BeforeAll
    void setVars() {
    	jobFactory = new JobFactory();
		execSvc = new JobExecutorService();
		execSvc.setJobFactory(jobFactory);
		IJobRegistryPopulator jrp = new JobRegistryPopulator().setFactory(jobFactory);
		final Properties props = new Properties();
		jrp.setConfiguration(props);
		jrp.registerJobs();
    }
    @Test
    void execSvcTest(){
        execSvc.setJobFactory(jobFactory);
        jobFactory.registerJobExecutor("Sample", new BaseJobExecutor());
        //execSvc.load();
        final IJobExecutor je = jobFactory.getJobExecutor("Sample");
        je.executeAsync("Arguments JobRegistryPopulatorTest", j -> {
            JobReturn jr = j; 
            logger.info("Job Return: {}", jr.getReturnValue());
            assertEquals(JobStatus.SUCCESS, jr.getJobStatus());
        	return j;
        });
    }

    @Test
    void executeTest(){
        execSvc.setJobFactory(jobFactory);
        jobFactory.registerJobExecutor("Sample", new BaseJobExecutor());
        //execSvc.load();
        final JobReturn jr = execSvc.execute("Test", "Sample", "Arguments");
        logger.info("Job Returned: {}", jr.getReturnValue());
        assertEquals(JobStatus.SUCCESS, jr.getJobStatus());
    }
}