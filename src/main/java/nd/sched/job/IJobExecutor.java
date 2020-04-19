package nd.sched.job;

public interface IJobExecutor {
    public enum JobStatus {IDLE, RUNNING, SUCCESS, FAILURE};
    public static class JobReturn {
        public String returnValue;
        public JobStatus jobStatus;
        @Override
        public String toString(){
            return returnValue + " - " + jobStatus;
        }
    };
    public String getName();
    public JobReturn execute(String argumentString);
}