package nd.sched.job;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandJobExecutor implements IJobExecutor {
    private static final Logger logger = LoggerFactory.getLogger(
        "nd.sched.job.service.run." + CommandJobExecutor.class.getName());

    private String name;
    private final List<String> osCommand;
    private String fullCommand;

    public CommandJobExecutor(){
        final String[] shell = OSCommand.getCommand();
        osCommand = Arrays.asList(shell);
    }

    @Override
    public String getName() {
        return name;
    }
    @Override
    public JobReturn execute(String argumentString) {
        final List<String> cmdList = new ArrayList<>();
        cmdList.addAll(osCommand);
        if (null != fullCommand) {
            cmdList.add(fullCommand);
        }
        cmdList.add(argumentString);
        final ProcessBuilder processBuilder = new ProcessBuilder(cmdList);
        logger.info("Executing: {}", processBuilder.command());
        final JobReturn jr = new JobReturn();
        final int ret = execute(processBuilder);
        switch (ret) {
            case 0:
                jr.jobStatus = JobStatus.SUCCESS;
                break;
            default:
                jr.jobStatus = JobStatus.FAILURE;
                break;
        }
        return jr;
    }
    public static int execute(final ProcessBuilder pb) {
        try {
            final Process process = pb.start();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while (null != (line = reader.readLine())) {
                logger.info(line);
            }
            int exitCode = process.waitFor();
            logger.info("Exit Code: {}", exitCode);
            return exitCode;
        } catch (IOException | InterruptedException e) {
            final String msg = "ERROR executing: " + pb.command().toString();
            logger.error(msg, e);
            return -1;
        }
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setFullCommand(final String cmd) {
        fullCommand = cmd;
    }
    public String getFullCommand(){
        return fullCommand;
    }
}