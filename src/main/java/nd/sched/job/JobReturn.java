package nd.sched.job;

public class JobReturn {
    public enum JobStatus {CREATED, RUNNING, SUCCESS, FAILURE}
    private String returnValue;
    private JobStatus jobStatus;
    @Override
    public String toString(){
        return returnValue + " - " + jobStatus;
    }

    public String getReturnValue() {
        return returnValue;
    }
    public void setReturnValue(String returnValue) {
        this.returnValue = returnValue;
    }
    public JobStatus getJobStatus() {
        return jobStatus;
    }
    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }
}
