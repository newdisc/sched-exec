package nd.sched.job.service;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import nd.sched.job.IJobExecutor.JobReturn;

public class AsyncExecutorFacade implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(AsyncExecutorFacade.class);
    IExecutorService service;
    ExecutorService javaExecutorService;

    public static class CallableWorker implements Supplier<JobReturn> {
        private static final Logger loggerIn = LoggerFactory.getLogger("nd.sched.job.service.run.CallableWorker");
        private static final String LOGFILENAME = "logFileName";
        protected String triggerName;
        protected String jobName;
        protected String arguments;
        protected IExecutorService service;
        public CallableWorker(final IExecutorService svc, final String tn, final String jn, 
            final String arg) {
            service = svc;
            triggerName = tn;
            jobName = jn;
            arguments = arg;
        }
        @Override
        public JobReturn get() {
            final Thread current = Thread.currentThread();
            final String name = triggerName + "-" + jobName + "-" + arguments + "-" + 
                current.getId() + "-" + Integer.toString((int)(Math.random() * 100));
            current.setName(name);
            MDC.put(LOGFILENAME,name);
            loggerIn.info("Starting Job: {} with arguments: {} on thread: {}", jobName, arguments, current.getId());
            final JobReturn jr = service.execute(jobName, arguments);
            loggerIn.info("Completed Job: {} with arguments: {} with return : {}", jobName, arguments, jr);
            MDC.remove(LOGFILENAME);
            return jr;
        }
    }

    public AsyncExecutorFacade() {
        int nThreads = Runtime.getRuntime().availableProcessors();
        javaExecutorService = Executors.newFixedThreadPool(nThreads);
    }

    @Override
    public void close() throws IOException {
        logger.info("Shutting down the fixed thread pool");
        javaExecutorService.shutdown();
    }
    public CompletableFuture<JobReturn> execute(final String triggerName, final String jobName, 
        final String arguments){
        final CallableWorker worker = new CallableWorker(service, triggerName, jobName, arguments);
        logger.info("Submitting Job: {} with arguments: {}", jobName, arguments);
        return CompletableFuture.supplyAsync(worker, javaExecutorService);
    }
    public IExecutorService getService() {
        return service;
    }
    public void setService(IExecutorService service) {
        this.service = service;
    }
    public ExecutorService getExecutor(){
        return javaExecutorService;
    }
}