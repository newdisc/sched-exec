package nd.sched.job.factory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import com.opencsv.CSVReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.CommandJobExecutor;
import nd.sched.job.FileCompareJobExecutor;
import nd.sched.job.JavaJobExecutor;

public class JobRegistryPopulator implements IJobRegistryPopulator{
    private static final Logger logger = LoggerFactory.getLogger(JobRegistryPopulator.class);
    private static final String EXECUTORS_FILE = "nd.sched.job.executors";
    private Properties configuration;
    private IJobFactory jobFactory;

	@Override
	public IJobRegistryPopulator setFactory(IJobFactory jf) {
        jobFactory = jf;
		return this;
	}

	@Override
    public IJobRegistryPopulator setConfiguration(Properties configuration) {
		this.configuration = configuration;
		return this;
	}

	@Override
    public void registerJobs() {
    	final String filename = configuration.getProperty(EXECUTORS_FILE, "jobExecutors.csv");
    	final File fileh = new File(filename);
    	if (!fileh.canRead()) {
    		logger.error("Missing executors list file: {}", filename);
    		return;
    	}
    	try (final CSVReader reader = new CSVReader(new FileReader(filename))){
    		reader.skip(1);//header
    		reader.forEach(line -> {
    			registerExecutor(line[0], line[1], Arrays.copyOfRange(line, 2, line.length));
    		});
    	} catch (IOException e) {
            final String msg = "Issue reading Job Executors File: " + filename;
            logger.error(msg, e);
    	}
    }
	@Override
	public IJobRegistryPopulator registerExecutor(final String type, final String name, final String[] arguments) {
		switch (type) {
		case "CommandJob":
			jobFactory.registerJobExecutor(name, 
					(new CommandJobExecutor()).setName(name).setFullCommand(arguments[0]));
			break;
		case "JavaJob":
			jobFactory.registerJobExecutor(name, 
					(new JavaJobExecutor()).setName(name).setMainClass(arguments[0]));
			break;
		case "CompareJob":
			jobFactory.registerJobExecutor(name, 
					(new FileCompareJobExecutor()).setName(name).setTemplate(arguments[0]));
			break;
		default:
			throw new RuntimeException("Unknown Type: " + type);
		}
		return this;
	}
}