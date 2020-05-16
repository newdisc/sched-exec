package nd.sched.job.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.IJobExecutor;
import nd.sched.job.IJobExecutor.JobReturn;

public class AsyncExecutorFacadeTest {
    private static final Logger logger = LoggerFactory.getLogger(AsyncExecutorFacadeTest.class);
   @Test
    public void runJobs(){
        try (TestAsyncEFCloseable asyncSvcTst = new TestAsyncEFCloseable()) {
            AsyncExecutorFacade asyncSvc = asyncSvcTst.asyncSvc;
            final Future<JobReturn> fjr = asyncSvc.execute("Trigger", "SleepJob", "15");
            final JobReturn jr = fjr.get();
            logger.info("Job Returned: {}", jr.getReturnValue());
            assertEquals(IJobExecutor.JobStatus.SUCCESS, jr.getJobStatus());
        } catch (IOException | InterruptedException | ExecutionException e) {
            final String msg = "Issue shutting down the async Service / running the task";    
            logger.error(msg, e);
        } finally {
            logger.info("Async Svc should have shutdown");
        }
    }
}