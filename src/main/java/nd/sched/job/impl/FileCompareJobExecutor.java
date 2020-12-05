package nd.sched.job.impl;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.UnaryOperator;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Diff;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.data.stream.DataStream;
import nd.data.stream.StringToInputStream;
import nd.sched.job.BaseJobExecutor;
import nd.sched.job.JobReturn;
import nd.sched.job.JobReturn.JobStatus;

public class FileCompareJobExecutor extends BaseJobExecutor {
	private static final Logger logger = LoggerFactory.getLogger("nd.sched.job.service.run." + 
			FileCompareJobExecutor.class.getSimpleName());
	private String templateText;

	@Override
    public void executeAsync(String argumentString, UnaryOperator<JobReturn> callBack) {
    	super.executeAsync(argumentString, callBack);
		final DiffMatchPatch dmp = new DiffMatchPatch();
		final String actualOut = fileToString(argumentString);
		final LinkedList<Diff> diff = dmp.diffMain(templateText, actualOut);
		diff.stream().forEach(dif -> {
			final String diffr = dif.toString();
			logger.info(diffr);
        	writeSafe(diffr.getBytes());
		});
		
		final JobReturn jr = new JobReturn();
		final Optional<Operation> op = diff.stream().map(dif -> dif.operation)
				.filter(opr -> Operation.EQUAL != opr).findAny();
		if (op.isPresent()) {
			jr.setJobStatus(JobStatus.FAILURE);
		} else {
			jr.setJobStatus(JobStatus.SUCCESS);
		}
        callBack.apply(jr);
	}

	public FileCompareJobExecutor setTemplate(String template) {
		final String CLASSPATH = "classpath:/";
		templateText = fileToString(CLASSPATH + template);
		return this;
	}

	public static String fileToString(String fileRes) {
		try (final StringToInputStream res = StringToInputStream.toInputStream(fileRes);
			 final DataStream ds = new DataStream(res);
				){
			return ds.streamLines().reduce((s1,s2) -> s1 + "\n" + s2).orElse("");
		} catch (Exception e) {
			logger.error("Could not initialize template string: {}", fileRes, e);
		}
		return "";
	}
}
