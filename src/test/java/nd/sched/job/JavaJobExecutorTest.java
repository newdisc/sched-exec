package nd.sched.job;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.sched.job.IJobExecutor.JobReturn;
import nd.sched.job.IJobExecutor.JobStatus;

public class JavaJobExecutorTest {
    private static final Logger logger = LoggerFactory.getLogger(JavaJobExecutorTest.class);

    private final JavaJobExecutor command = new JavaJobExecutor();
    @BeforeEach
    public void init(){
        command.setMainClass("nd.sched.job.JavaJobExecutorTest");
    }

    @Test
    public void execTest(){
        JobReturn jr = command.execute("a b c");
        logger.info("Job result: {}", jr.getReturnValue());
        assertEquals(JobStatus.SUCCESS, jr.getJobStatus());
    }

    public static void main(final String[] args) {
    	logger.info("Executing class with args: {}", 
    			Arrays.stream(args).collect(Collectors.joining("-")));
    }
}