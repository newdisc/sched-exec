package nd.sched.job.service;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        private static final Logger loggerIn = LoggerFactory.getLogger("nd.sched.job.service.run." + CallableWorker.class.getSimpleName());
        private static final String LOGFILENAME = "logFileName";
        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        protected String triggerName;
        protected String jobName;
        protected String arguments;
        protected IExecutorService service;
        private static int i=0;
        public CallableWorker(final IExecutorService svc, final String tn, final String jn, 
            final String arg) {
            service = svc;
            triggerName = tn;
            jobName = jn;
            arguments = arg;
        }
        @SuppressWarnings("squid:S2696")
        @Override
        public JobReturn get() {
            final Thread current = Thread.currentThread();
            final LocalDateTime now = LocalDateTime.now();
            final String name = now.format(formatter) + "-" + current.getId() + "-" + triggerName + "-" + jobName + "-"; 
            current.setName(triggerName + current.getId());
            MDC.put(LOGFILENAME,name);
            loggerIn.debug("Starting Job: {} with arguments: {} on thread: {}", jobName, arguments, current.getId());
            loggerIn.info("=======================================================");
            final JobReturn jr = service.execute(jobName, arguments);
            loggerIn.info("=======================================================");
            loggerIn.debug("Completed Job: {} with arguments: {} with return : {}", jobName, arguments, jr);
            MDC.remove(LOGFILENAME);
            final String thrName = "AsyncExecutorthread-" + i;
            i++;
            current.setName(thrName);
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
        logger.debug("Submitting Job: {} with arguments: {}", jobName, arguments);
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