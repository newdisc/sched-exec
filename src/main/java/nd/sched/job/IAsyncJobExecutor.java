package nd.sched.job;

import java.util.function.Function;

public interface IAsyncJobExecutor extends IJobExecutor {
    public void executeAsync(String argumentString, Function<JobReturn, JobReturn> callBack);
    public Function<JobReturn,JobReturn> getCallback();
}
