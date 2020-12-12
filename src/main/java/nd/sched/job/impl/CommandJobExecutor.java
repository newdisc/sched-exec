package nd.sched.job.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import nd.sched.job.BaseJobExecutor;
import nd.sched.job.JobReturn;
import nd.sched.job.JobReturn.JobStatus;

public class CommandJobExecutor extends BaseJobExecutor {
    private static final Logger logger = LoggerFactory.getLogger(
        "nd.sched.job.service.run." + CommandJobExecutor.class.getSimpleName());

    @JsonIgnore
    private final List<String> osCommand;
    private String fullCommand;

    public CommandJobExecutor(){
        final String[] shell = OSCommand.getCommand();
        osCommand = Arrays.asList(shell);
    }

    @Override
    public void executeAsync(String argumentString, UnaryOperator<JobReturn> callBack) {
    	super.executeAsync(argumentString, callBack);
        final List<String> cmdList = new ArrayList<>();
        cmdList.addAll(osCommand);
        String execCommand = "";
        if (null != fullCommand) {
        	execCommand = fullCommand;
        }
        execCommand = execCommand + " " + argumentString;
        cmdList.add(execCommand);
        final ProcessBuilder processBuilder = new ProcessBuilder(cmdList);
        logger.info("#Executing: {}", processBuilder.command());
        final JobReturn jr = new JobReturn();
        final int ret = execute(processBuilder);
        jr.setJobStatus(JobStatus.FAILURE);
        if (0 == ret) {
            jr.setJobStatus(JobStatus.SUCCESS);
        }
        callBack.apply(jr);
    }

    public int execute(final ProcessBuilder pb) {
        try {
        	pb.redirectErrorStream(true);
        	//pb.redirectOutput();
            final Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while (null != (line = reader.readLine())) {
            	writeSafe(line.getBytes());
                logger.info(line);
            }
            int exitCode = process.waitFor();
            logger.info("#Exit Code: {}", exitCode);
            return exitCode;
        } catch (IOException | InterruptedException e) {
            final String msg = "ERROR executing: " + pb.command().toString();
            writeSafe(msg.getBytes());
            logger.error(msg, e);
            return -1;
        }
    }
    public CommandJobExecutor setFullCommand(final String cmd) {
        fullCommand = cmd;
        return this;
    }
    public String getFullCommand(){
        return fullCommand;
    }
}
