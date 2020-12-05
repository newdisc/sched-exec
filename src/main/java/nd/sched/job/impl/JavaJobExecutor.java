package nd.sched.job.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.BaseJobExecutor;
import nd.sched.job.JobReturn;

public class JavaJobExecutor extends BaseJobExecutor {
    private static final Logger logger = LoggerFactory.getLogger(
        "nd.sched.job.service.run." + JavaJobExecutor.class.getSimpleName());

    private String mainClass;
    private Method mainMethod;
    @Override
    public void executeAsync(String argumentString, UnaryOperator<JobReturn> callBack) {
    	super.executeAsync(argumentString, callBack);
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
        callBack.apply(jr);
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
	public String getMainClass() {
		return mainClass;
	}
}
