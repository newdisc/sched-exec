package nd.sched.job.factory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import com.opencsv.CSVReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.impl.CommandJobExecutor;
import nd.sched.job.impl.FileCompareJobExecutor;
import nd.sched.job.impl.JavaJobExecutor;
import nd.sched.job.impl.TimerJobExecutor;
import nd.sched.util.UtilException;

public class JobRegistryPopulator {
    private static final Logger logger = LoggerFactory.getLogger(JobRegistryPopulator.class);
    private static final String EXECUTORS_FILE = "nd.sched.job.executors";
    private Properties configuration;
    private IJobFactory jobFactory;

	public JobRegistryPopulator setFactory(IJobFactory jf) {
        jobFactory = jf;
		return this;
	}

    public JobRegistryPopulator setConfiguration(Properties configuration) {
		this.configuration = configuration;
		return this;
	}

    public void registerJobs() {
    	final String filename = configuration.getProperty(EXECUTORS_FILE, "jobExecutors.csv");
    	final File fileh = new File(filename);
    	if (!fileh.canRead()) {
    		logger.error("Missing executors list file: {}", filename);
    		return;
    	}
    	try (final CSVReader reader = new CSVReader(new FileReader(filename))){
    		reader.skip(1);//header
    		reader.forEach(line -> 
    			registerExecutor(line[0], line[1], Arrays.copyOfRange(line, 2, line.length))
    		);
    	} catch (IOException e) {
            final String msg = "Issue reading Job Executors File: " + filename;
            logger.error(msg, e);
    	}
    }

	public JobRegistryPopulator registerExecutor(final String type, final String name, final String[] arguments) {
		switch (type) {
		case "CommandJob":
			registerCommandJob(name, arguments[0]);
			break;
		case "JavaJob":
			registerJavaJob(name, arguments[0]);
			break;
		case "CompareJob":
			registerFileCompareJob(name, arguments[0]);
			break;
		case "CronJob":
			registerCronJob(name, arguments[0], arguments[1]);
			break;
		default:
			throw new UtilException("Unknown Type: " + type);
		}
		return this;
	}

	protected void registerCronJob(final String name, final String cron, final String tz) {
		final TimerJobExecutor tje = new TimerJobExecutor();
		tje.setCronCondition(cron).setTimeZone(tz).setName(name);
		jobFactory.registerJobExecutor(name, tje);
	}

	protected void registerFileCompareJob(final String name, final String template) {
		final FileCompareJobExecutor fcje = new FileCompareJobExecutor();
		fcje.setTemplate(template).setName(name);
		jobFactory.registerJobExecutor(name, fcje);
	}

	protected void registerJavaJob(final String name, final String mc) {
		final JavaJobExecutor jje = new JavaJobExecutor();
		jje.setMainClass(mc).setName(name);
		jobFactory.registerJobExecutor(name, jje);
	}

	protected void registerCommandJob(final String name, final String cmd) {
		final CommandJobExecutor cje = new CommandJobExecutor();
		cje.setFullCommand(cmd).setName(name);
		jobFactory.registerJobExecutor(name, cje);
	}
}