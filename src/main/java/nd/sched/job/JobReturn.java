package nd.sched.job;

import nd.sched.job.IJobExecutor.JobStatus;

public class JobReturn {
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
