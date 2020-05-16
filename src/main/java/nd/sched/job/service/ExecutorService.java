package nd.sched.job.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.IJobExecutor;
import nd.sched.job.IJobExecutor.JobReturn;
import nd.sched.job.IJobExecutor.JobStatus;
import nd.sched.job.factory.IJobFactory;
import nd.sched.job.factory.IJobRegistryPopulator;

public class ExecutorService implements IExecutorService {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorService.class);
    private static final String JOB_REGISTRY_POP_PACKAGE = "nd.sched.job.factory";
    private static final String BASE_DIR = ".";
    private IJobFactory jobFactory;
    private List<IJobRegistryPopulator> jobRegistryPopulators;

    public void load() {
        Reflections reflections = new Reflections(JOB_REGISTRY_POP_PACKAGE);
        Set<Class<? extends IJobRegistryPopulator>> subTypes = reflections.getSubTypesOf(IJobRegistryPopulator.class);
        jobRegistryPopulators = subTypes
            .stream()
            .map(this::createPopulatorInstance)
            //.map(cls -> createPopulatorInstance(cls))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        jobRegistryPopulators.forEach(IJobRegistryPopulator::registerJobs);
    }

    private IJobRegistryPopulator createPopulatorInstance(Class<? extends IJobRegistryPopulator> cls){try {
        logger.debug("Loading: {}", cls.getName());
        return cls
            .getDeclaredConstructor(IJobFactory.class, String.class)
            .newInstance(jobFactory, BASE_DIR);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException | NoSuchMethodException | SecurityException e) {
        final String msg = "Could NOT instantiate Populator: ";
        logger.error(msg, e);
        return null;
    }}

    @Override
    public JobReturn execute(final String jobName, final String arguments) {
        final IJobExecutor job = jobFactory.getJobExecutor(jobName);
        final JobReturn jr;
        if (null!=job) {
            jr = job.execute(arguments);
        } else {
            jr = new JobReturn();
            jr.setJobStatus(JobStatus.FAILURE);
            jr.setReturnValue("Job NOT found!");
        }
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