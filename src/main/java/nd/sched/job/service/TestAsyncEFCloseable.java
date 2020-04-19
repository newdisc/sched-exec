package nd.sched.job.service;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.factory.JobFactory;

public class TestAsyncEFCloseable implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(TestAsyncEFCloseable.class);
    public AsyncExecutorFacade asyncSvc;
    public TestAsyncEFCloseable(){
        final String cwd = Paths.get(".")
            .toAbsolutePath()
            .normalize()
            .toString();
        logger.info("CWD: {}", cwd);
        ExecutorService execSvc = new ExecutorService();
        JobFactory jobFactory = new JobFactory();
        execSvc.setJobFactory(jobFactory);
        execSvc.load();
        asyncSvc = new AsyncExecutorFacade();
        asyncSvc.setService(execSvc);
    }
    @Override
    public void close() throws IOException {
       asyncSvc.close();
    }
}