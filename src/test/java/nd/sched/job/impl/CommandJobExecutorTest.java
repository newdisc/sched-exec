package nd.sched.job.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.IJobExecutor.JobStatus;
import nd.sched.job.JobReturn;

class CommandJobExecutorTest {
    private static final Logger logger = LoggerFactory.getLogger(CommandJobExecutorTest.class);

    private final CommandJobExecutor command = new CommandJobExecutor();
    @BeforeEach
    public void init(){
        command.setFullCommand("set");
    }

    @Test
    void execTest(){
        command.executeAsync("", j -> {
            JobReturn jr = j; 
            logger.info("Job Return: {}", jr.getReturnValue());
            assertEquals(JobStatus.SUCCESS, jr.getJobStatus());
        	return j;
        });
    }

}