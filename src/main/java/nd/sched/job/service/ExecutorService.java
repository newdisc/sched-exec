package nd.sched.job.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.IJobExecutor;
import nd.sched.job.IJobExecutor.JobReturn;
import nd.sched.job.factory.IJobFactory;
import nd.sched.job.factory.IJobRegistryPopulator;

public class ExecutorService {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorService.class);
    private static final String JOB_REGISTRY_POP_PACKAGE = "nd.sched.job.factory";
    private static final String BASE_DIR = ".";
    private IJobFactory jobFactory;
    public List<IJobRegistryPopulator> jobRegistryPopulators;

    public void load() {
        Reflections reflections = new Reflections(JOB_REGISTRY_POP_PACKAGE);
        Set<Class<? extends IJobRegistryPopulator>> subTypes = reflections.getSubTypesOf(IJobRegistryPopulator.class);
        jobRegistryPopulators = subTypes
            .stream()
            .map(cls -> extracted(cls))
            .filter(populator -> (null != populator))
            .collect(Collectors.toList());
        jobRegistryPopulators.forEach(p -> p.registerJobs());
    }

    private IJobRegistryPopulator extracted(Class<? extends IJobRegistryPopulator> cls){try {
        logger.info("Loading: {}", cls.getName());
        return cls
            .getDeclaredConstructor(IJobFactory.class, String.class)
            .newInstance(jobFactory, BASE_DIR);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException | NoSuchMethodException | SecurityException e) {
        final String msg = "Could NOT instantiate Populator: ";
        logger.error(msg, e);
        return null;
    }}

    public JobReturn execute(final String jobName, final String arguments) {
        final IJobExecutor job = jobFactory.getJobExecutor(jobName);
        final JobReturn jr = job.execute(arguments);
        return jr;
    }

    public IJobFactory getJobFactory() {
        return jobFactory;
    }
    public void setJobFactory(IJobFactory jobFactory) {
        this.jobFactory = jobFactory;
    }
    public List<IJobRegistryPopulator> getJobRegistryPopulators() {
        return jobRegistryPopulators;
    }
    public void setJobRegistryPopulators(List<IJobRegistryPopulator> jobRegistryPopulators) {
        this.jobRegistryPopulators = jobRegistryPopulators;
    }
}