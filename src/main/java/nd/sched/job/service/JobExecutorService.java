package nd.sched.job.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.data.util.IConfigurable;
import nd.sched.job.BaseJobExecutor;
import nd.sched.job.JobReturn;
import nd.sched.job.JobReturn.JobStatus;
import nd.sched.job.factory.IJobFactory;

public class JobExecutorService implements IJobExecutorService {
    private static final Logger logger = LoggerFactory.getLogger(JobExecutorService.class);
    public static final String CFG_OUT_DIR = "nd.sched.job.output";
    public static final String DEFAULT_LOGDIR = "./logs/";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private IJobFactory jobFactory;
    private Properties properties;

	@Override
	public void initiateExecute(String triggerName, String jobName, String arguments,
			UnaryOperator<JobReturn> callBack) {
		logger.info("===Executing: {}", triggerName);
        final BaseJobExecutor job = jobFactory.getJobExecutor(jobName);
        if (null == job) {
        	handleError("Job NOT found: {}, {}", jobName, callBack);
            return;
        }
		final String outname = properties.getProperty(CFG_OUT_DIR, DEFAULT_LOGDIR) + 
				LocalDate.now().format(DATE_FMT) + "-" + triggerName + "-" + jobName + ".out";
		final BufferedOutputStream bos = setJobStream(job, outname);
        if (null == job.getStream()) {
        	handleError("Output NOT found: {}, {}", outname, callBack);
            return;
        }
        job.setStream(bos);
        job.executeAsync(arguments, jr -> {
        	callBack.apply(jr);
        	try {
				bos.close();
			} catch (IOException e) {
				logger.error("Problem writing output with executing job: ", e);
			}
        	return jr;
        });
	}
	@Override
	public JobReturn execute(String triggerName, String jobName, String arguments) {
		final CompletableFuture<JobReturn> jfut = new CompletableFuture<>();
		initiateExecute(triggerName, jobName, arguments, jr -> {
			jfut.complete(jr);
			return jr;
		});
		JobReturn jr;
		jr = waitForJobReturn(jfut);
		return jr;
	}

	protected BufferedOutputStream setJobStream(final BaseJobExecutor job, final String outname) {
		final BufferedOutputStream bos;
		try {
			final File file = new File(outname);
			logger.info(file.getCanonicalPath());
			bos = new BufferedOutputStream(new FileOutputStream(outname));
			job.setStream(bos);
		} catch (IOException e) {
            return null;
		}
		return bos;
	}
	protected void handleError(final String error, String jobName, UnaryOperator<JobReturn> callBack) {
		logger.error(error, jobName);
		final JobReturn jr = new JobReturn();
		jr.setJobStatus(JobStatus.FAILURE);
		jr.setReturnValue("Job/output NOT found!");
		callBack.apply(jr);
	}
	protected JobReturn waitForJobReturn(final CompletableFuture<JobReturn> jfut) {
		JobReturn jr;
		try {
			jr = jfut.get();
		} catch (ExecutionException e) {
			logger.error("Problem with executing job: ", e);
			jr = new JobReturn();
			jr.setJobStatus(JobStatus.FAILURE);
			jr.setReturnValue("Exception: " + e.getMessage());
		} catch (InterruptedException e) {
			logger.error("Interrupted while executing job: ", e);
			jr = new JobReturn();
			jr.setJobStatus(JobStatus.FAILURE);
			jr.setReturnValue("Exception: " + e.getMessage());
			Thread.currentThread().interrupt();
		}
		return jr;
	}
	@Override
	public IConfigurable setConfig(Properties props) {
		properties = props;
		return this;
	}
    public IJobFactory getJobFactory() {
        return jobFactory;
    }
    public JobExecutorService setJobFactory(IJobFactory jobFactory) {
        this.jobFactory = jobFactory;
        return this;
    }
}
