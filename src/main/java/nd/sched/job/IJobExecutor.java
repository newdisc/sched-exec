package nd.sched.job;

import java.io.OutputStream;
import java.util.function.UnaryOperator;

public interface IJobExecutor {
    public String getName();
    public void executeAsync(String argumentString, UnaryOperator<JobReturn> callBack);
    public UnaryOperator<JobReturn> getCallback();
    public IJobExecutor setStream(OutputStream os);
}