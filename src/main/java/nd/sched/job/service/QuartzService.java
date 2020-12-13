package nd.sched.job.service;

import java.io.Closeable;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.UnaryOperator;

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

import nd.sched.job.impl.TimerJobExecutor;
import nd.sched.job.JobReturn;
import nd.sched.job.JobReturn.JobStatus;

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
	
	public static class QSJobDetail implements Job {
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			final Date nft = context.getNextFireTime();

			final JobReturn jr = new JobReturn();
			final String nftstr = "Next Firetime: " + DFORMAT.format(nft);
			jr.setReturnValue(nftstr);
			jr.setJobStatus(JobStatus.SUCCESS);
			
			final JobDataMap jobDataMap = context.getMergedJobDataMap(); 
	        final TimerJobExecutor tje = (TimerJobExecutor)jobDataMap.get("timerJobExecutor");
	        final UnaryOperator<JobReturn> callback = tje.getCallback();
	        JobReturn res = callback.apply(jr);
	        tje.writeSafe(nftstr.getBytes());
	        logger.info("Callback to SUCCESS returned: {}", res);

	        jr.setJobStatus(JobStatus.RUNNING);
	        res = callback.apply(jr);
	        logger.info("Callback to RUNNING returned: {}", res);
		}
	}

	public String addJob(TimerJobExecutor tje) {
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
			final String nftstr = "Next Firetime: " + DFORMAT.format(dt);
            logger.info("Scheduled job: {} with schedule: {} and date: {}", 
                name, condition, dt);
            return nftstr;
        } catch (SchedulerException e) {
            final String msg = "Unable to add time schedule for job: " + name;
            logger.error(msg, e);
            return msg;
        }
    }
}
