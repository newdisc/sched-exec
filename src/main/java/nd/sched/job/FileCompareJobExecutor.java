package nd.sched.job;

import java.util.LinkedList;
import java.util.Optional;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Diff;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.data.stream.DataStream;
import nd.data.stream.StringToInputStream;

public class FileCompareJobExecutor implements IJobExecutor {
	private static final Logger logger = LoggerFactory.getLogger("nd.sched.job.service.run." + 
			FileCompareJobExecutor.class.getSimpleName());
	private String templateText;
	private String name;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public JobReturn execute(String argumentString) {
		final DiffMatchPatch dmp = new DiffMatchPatch();
		final String actualOut = fileToString(argumentString);
		final LinkedList<Diff> diff = dmp.diffMain(templateText, actualOut);
		diff.stream().forEach(dif -> logger.info(dif.toString()));
		
		final JobReturn jr = new JobReturn();
		final Optional<Operation> op = diff.stream().map(dif -> dif.operation)
				.filter(opr -> Operation.EQUAL != opr).findAny();
		if (op.isPresent()) {
			jr.setJobStatus(JobStatus.FAILURE);
		} else {
			jr.setJobStatus(JobStatus.SUCCESS);
		}
		return jr;
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
	public FileCompareJobExecutor setName(String name) {
		this.name = name;
		return this;
	}
}
