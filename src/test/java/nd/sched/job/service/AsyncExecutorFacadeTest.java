package nd.sched.job.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.IJobExecutor;
import nd.sched.job.IJobExecutor.JobReturn;
import nd.sched.job.factory.IJobFactory;
import nd.sched.job.factory.JobFactory;

public class AsyncExecutorFacadeTest {
    private static final Logger logger = LoggerFactory.getLogger(AsyncExecutorFacadeTest.class);
    @Test
    public void runJobs(){
        final String cwd = Paths.get(".")
            .toAbsolutePath()
            .normalize()
            .toString();
        logger.info("CWD: {}", cwd);
        ExecutorService execSvc = new ExecutorService();
        IJobFactory jobFactory = new JobFactory();
        execSvc.setJobFactory(jobFactory);
        execSvc.load();
        try (AsyncExecutorFacade asyncSvc = new AsyncExecutorFacade();) {
            asyncSvc.setService(execSvc);
            final Future<JobReturn> fjr = asyncSvc.execute("SleepJob", "15");
            final JobReturn jr = fjr.get();
            logger.info("Job Returned: {}", jr.returnValue);
            assertEquals(jr.jobStatus, IJobExecutor.JobStatus.SUCCESS);
        } catch (IOException | InterruptedException | ExecutionException e) {
            final String msg = "Issue shutting down the async Service / running the task";    
            logger.error(msg, e);
        } finally {
            logger.info("Async Svc should have shutdown");
        }
    }
}