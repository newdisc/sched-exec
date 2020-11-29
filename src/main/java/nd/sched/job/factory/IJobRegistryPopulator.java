package nd.sched.job.factory;

import java.util.Properties;

public interface IJobRegistryPopulator {
    public IJobRegistryPopulator setFactory(IJobFactory jobfactory);
    public IJobRegistryPopulator setConfiguration(Properties configuration);

    public void registerJobs();
    public IJobRegistryPopulator registerExecutor(final String type, final String name, final String[] arguments);
}