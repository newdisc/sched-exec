package nd.sched.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobExecutor implements IJobExecutor {
    private static Logger logger = LoggerFactory.getLogger(JobExecutor.class);
    private String name;
    public void setName(final String nm){
        name = nm;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public JobReturn execute(String argumentString) {
        logger.info("Hello World from {}", name);
        JobReturn jReturn = new JobReturn();
        jReturn.jobStatus = JobStatus.SUCCESS;
        jReturn.returnValue = "SUCCESS";
        return jReturn;
    }
    @Override
    public String toString(){
        return name;
    }
}