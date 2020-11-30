package nd.sched.job;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaJobExecutor implements IJobExecutor {
    private static final Logger logger = LoggerFactory.getLogger(
        "nd.sched.job.service.run." + JavaJobExecutor.class.getSimpleName());

    private String name;
    private String mainClass;
    private Method mainMethod;
    @Override
    public JobReturn execute(String argumentString) {
        final JobReturn jr = new JobReturn();
        jr.setJobStatus(JobStatus.FAILURE);
        String[] arguments = argumentString.split(",", -1);
        final String argsString = Arrays.toString(arguments);
        logger.info("Invoking {}.main {}", mainClass, argsString);
    	try {
			Object result = mainMethod.invoke(null, (Object)arguments);
	        jr.setJobStatus(JobStatus.SUCCESS);
	        logger.info("Returned");
	        logger.debug("Returned: {}", result);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            final String msg = "ERROR executing: " + mainClass + ".main " + argsString;
            logger.error(msg, e);
		}
        return jr;
    }

	public JavaJobExecutor setMainClass(String mainClass) {
		this.mainClass = mainClass;
		try {
	    	final Class<? extends Object> myClass = Class.forName(mainClass);
	    	mainMethod = myClass.getMethod("main", String[].class);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
            final String msg = "ERROR finding: " + mainClass + ".main";
            logger.error(msg, e);
		}
		return this;
	}
    @Override
    public String getName() {
        return name;
    }
    public JavaJobExecutor setName(String name) {
        this.name = name;
        return this;
    }
	public String getMainClass() {
		return mainClass;
	}
}
