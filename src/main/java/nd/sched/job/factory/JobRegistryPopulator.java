package nd.sched.job.factory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import com.opencsv.CSVReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.BaseJobExecutor;
import nd.sched.job.impl.CommandJobExecutor;
import nd.sched.job.impl.FileCompareJobExecutor;
import nd.sched.job.impl.HttpTableClientExecutor;
import nd.sched.job.impl.JavaJobExecutor;
import nd.sched.job.impl.TimerJobExecutor;
import nd.sched.job.service.QuartzService;
import nd.sched.util.UtilException;

public class JobRegistryPopulator {
    private static final Logger logger = LoggerFactory.getLogger(JobRegistryPopulator.class);
    private static final String EXECUTORS_FILE = "nd.sched.job.executors";
    private Properties configuration;
    private IJobFactory jobFactory;
    private QuartzService quartzService;

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
        	logger.info("File being used: {}", fileh.getCanonicalPath());
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
		final BaseJobExecutor bje;
		switch (type) {
		case CommandJobExecutor.TYPE:
			bje = registerCommandJob(name, arguments[0]);
			break;
		case JavaJobExecutor.TYPE:
			bje = registerJavaJob(name, arguments[0]);
			break;
		case FileCompareJobExecutor.TYPE:
			bje = registerFileCompareJob(name, arguments[0]);
			break;
		case TimerJobExecutor.TYPE:
			bje = registerCronJob(name, arguments[0], arguments[1]);
			break;
		case HttpTableClientExecutor.TYPE:
			bje = registerHttpTableJob(name, arguments);
			break;
		default:
			throw new UtilException("Unknown Type: " + type);
		}
		bje.setType(type);
		return this;
	}

	private HttpTableClientExecutor registerHttpTableJob(String name, String[] arguments) {
		final HttpTableClientExecutor hce = new HttpTableClientExecutor();
		for (int i = 0; i < arguments.length; i++) {
			switch(i) {
			case 0:
				hce.setUrl(arguments[i]);
				break;
			case 1:
				hce.setTableSelector(arguments[i]);
				break;
			case 2:
				hce.setColumn(arguments[i]);
				break;
			case 3:
				hce.setRow(arguments[i]);
				break;
			case 4:
				hce.setHeader(arguments[i]);
				break;
			default:
				break;
			}
		}
		jobFactory.registerJobExecutor(name, hce);
		return hce;
	}

	protected TimerJobExecutor registerCronJob(final String name, final String cron, final String tz) {
		final TimerJobExecutor tje = new TimerJobExecutor();
		tje.setCronCondition(cron).setTimeZone(tz).setQuartzService(quartzService).setName(name);
		jobFactory.registerJobExecutor(name, tje);
		return tje;
	}

	protected FileCompareJobExecutor registerFileCompareJob(final String name, final String template) {
		final FileCompareJobExecutor fcje = new FileCompareJobExecutor();
		fcje.setTemplate(template).setName(name);
		jobFactory.registerJobExecutor(name, fcje);
		return fcje;
	}

	protected JavaJobExecutor registerJavaJob(final String name, final String mc) {
		final JavaJobExecutor jje = new JavaJobExecutor();
		jje.setMainClass(mc).setName(name);
		jobFactory.registerJobExecutor(name, jje);
		return jje;
	}

	protected CommandJobExecutor registerCommandJob(final String name, final String cmd) {
		final CommandJobExecutor cje = new CommandJobExecutor();
		cje.setFullCommand(cmd).setName(name);
		jobFactory.registerJobExecutor(name, cje);
		return cje;
	}

	public void setQuartzService(QuartzService quartzService) {
		this.quartzService = quartzService;
	}
}