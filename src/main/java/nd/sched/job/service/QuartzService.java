package nd.sched.job.service;

import java.io.Closeable;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.Function;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.TimerJobExecutor;
import nd.sched.job.IJobExecutor.JobReturn;
import nd.sched.job.IJobExecutor.JobStatus;

public class QuartzService implements Closeable {
	private static final Logger logger = LoggerFactory.getLogger(QuartzService.class);
	private static final DateFormat DFORMAT = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS"); 
    private final SchedulerFactory schedulerFactory;
    private final Scheduler scheduler;

    public QuartzService() throws SchedulerException {
        schedulerFactory = new StdSchedulerFactory();
        scheduler = schedulerFactory.getScheduler();
        scheduler.start();
    }
	@Override
	public void close() throws IOException {
        try {
            logger.info("Shutting down Quartz Scheduler");
            scheduler.shutdown(true);
        } catch (SchedulerException e) {
            final String msg = "Unable to Shutdown scheduler";
            logger.error(msg, e);
        }
	}
	
	public class QSJobDetail implements Job {
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			final Date nft = context.getNextFireTime();

			final JobReturn jr = new JobReturn();
			jr.setReturnValue("Next Firetime: " + DFORMAT.format(nft));
			jr.setJobStatus(JobStatus.SUCCESS);
			
			final JobDataMap jobDataMap = context.getMergedJobDataMap(); 
	        final TimerJobExecutor tje = (TimerJobExecutor)jobDataMap.get("timerJobExecutor");
	        final Function<JobReturn, JobReturn> callback = tje.getCallback();
	        JobReturn res = callback.apply(jr);
	        logger.info("Callback to SUCCESS returned: {}", res);

	        jr.setJobStatus(JobStatus.RUNNING);
	        res = callback.apply(jr);
	        logger.info("Callback to RUNNING returned: {}", res);
		}
	}

	public void addJob(TimerJobExecutor tje) {
		final String condition = tje.getCronCondition();
        final String name = tje.getName() + "_quartz";
        final JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("timerJobExecutor", tje);
        JobDetail jd = JobBuilder
            .newJob(QSJobDetail.class)
            .usingJobData(jobDataMap)
            .withIdentity(name, "TimerJobExecutors")
            .withDescription(name)
            .build();
        final CronScheduleBuilder csb = CronScheduleBuilder.cronSchedule(condition);
        csb.inTimeZone(TimeZone.getTimeZone(tje.getTimeZone()));
        final Trigger trg = TriggerBuilder
            .newTrigger()
            .withIdentity(name, "TimerTriggers")
            .withSchedule(csb)
            .build();

        try {
            Date dt = scheduler.scheduleJob(jd, trg);
            logger.info("Scheduled job: {} with schedule: {} and date: {}", 
                name, condition, dt);
        } catch (SchedulerException e) {
            final String msg = "Unable to add time schedule for job: " + name;
            logger.error(msg, e);
        }
    }
}
