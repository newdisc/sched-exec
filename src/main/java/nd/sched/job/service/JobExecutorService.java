package nd.sched.job.service;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.data.util.IConfigurable;
import nd.sched.job.IJobExecutor;
import nd.sched.job.JobReturn;
import nd.sched.job.IJobExecutor.JobStatus;
import nd.sched.job.factory.IJobFactory;

public class JobExecutorService implements IJobExecutorService {
    private static final Logger logger = LoggerFactory.getLogger(JobExecutorService.class);
    private static final String CFG_OUT_DIR = "nd.sched.job.output";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private IJobFactory jobFactory;
    private Properties properties;

	@Override
	public void initiateExecute(String triggerName, String jobName, String arguments,
			UnaryOperator<JobReturn> callBack) {
		final String outname = properties.getProperty(CFG_OUT_DIR, "out/") + 
				LocalDate.now().format(DATE_FMT) + "-" + triggerName + "-" + jobName + ".out";
        final IJobExecutor job = jobFactory.getJobExecutor(jobName);
		BufferedOutputStream bos;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(outname));
		} catch (FileNotFoundException e) {
			bos = null;
		}
        if (null == job || null == bos) {
        	logger.error("Job/output NOT found: {}, {}", jobName, outname);
            final JobReturn jr = new JobReturn();
            jr.setJobStatus(JobStatus.FAILURE);
            jr.setReturnValue("Job/output NOT found!");
            callBack.apply(jr);
        }
        job.setStream(bos);
        job.executeAsync(arguments, jr -> {
        	callBack.apply(jr);
        	return jr;
        });
	}
	@Override
	public JobReturn execute(String triggerName, String jobName, String arguments) {
		final CompletableFuture<JobReturn> jfut = new CompletableFuture<JobReturn>();
		initiateExecute(triggerName, jobName, arguments, jr -> {
			jfut.complete(jr);
			return jr;
		});
		JobReturn jr;
		try {
			jr = jfut.get();
		} catch (InterruptedException | ExecutionException e) {
			logger.error("Problem with executing job: ", e);
			jr = new JobReturn();
			jr.setJobStatus(JobStatus.FAILURE);
			jr.setReturnValue("Exception: " + e.getMessage());
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
