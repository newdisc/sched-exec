package nd.sched.job;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.IJobExecutor.JobReturn;
import nd.sched.job.IJobExecutor.JobStatus;

public class CommandJobExecutorTest {
    private static final Logger logger = LoggerFactory.getLogger(CommandJobExecutorTest.class);

    private final CommandJobExecutor command = new CommandJobExecutor();
    @BeforeEach
    public void init(){
        command.setFullCommand("set");
    }

    @Test
    public void execTest(){
        JobReturn jr = command.execute("");
        logger.info("Job result: {}", jr.returnValue);
        assertEquals(jr.jobStatus, JobStatus.SUCCESS);
    }

}