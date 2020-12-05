package nd.sched.job.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import nd.sched.job.IJobExecutor.JobReturn;

public class AsyncLogExecutorService extends JobExecutorService implements ILogJobExecutorService {
	private static final Logger logger = LoggerFactory.getLogger(AsyncLogExecutorService.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String LOGFILENAME = "logFileName";
	private ExecutorService javaExecutor;

	@Override
	public CompletableFuture<JobReturn> initiateExecute(final String triggerName, final String jobName, 
			final String arguments) {
    	logger.info("Initiating: {}-{}, {}", triggerName, jobName, arguments);
    	//CompletableFuture<JobReturn> cfjr = new CompletableFuture<>();
    	return CompletableFuture.supplyAsync(() -> { return logAndExecute(triggerName, jobName,arguments);}, javaExecutor);
    }

	@Override
	public JobReturn logAndExecute(final String triggerName, final String jobName, 
			final String arguments) {
        final Thread current = Thread.currentThread();
        final LocalDateTime now = LocalDateTime.now();
        final String name = now.format(formatter) + "-" + triggerName + "-" + jobName + "-" + current.getId();
        final String origName = current.getName();
        current.setName(triggerName + current.getId());
        MDC.put(LOGFILENAME,name);
        logger.info("Starting Job: {} with arguments: {}, origName: {}", name, arguments, origName);
        logger.info("=======================================================");
        final JobReturn jr = execute(jobName, arguments);
        logger.info("=======================================================");
        logger.info("Completed Job: {} with arguments: {} with return : {}", name, arguments, jr);
        MDC.remove(LOGFILENAME);
        current.setName(origName);
        return jr;
	}

	@Override
	public void setJavaExecutor(ExecutorService javaExecutor) {
		this.javaExecutor = javaExecutor;
	}
}
