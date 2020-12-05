package nd.sched.job;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseJobExecutor implements IJobExecutor, Closeable {
	private static final Logger logger = LoggerFactory.getLogger(BaseJobExecutor.class);
	protected String name;
	protected UnaryOperator<JobReturn> callBack;
	protected OutputStream oStream;

	public void writeSafe(final byte[] str) {
		try {
			oStream.write(str);
		} catch (IOException e) {
			logger.error("Could not write to outputStream: {}", str, e);
		}
	}
	@Override
	public void executeAsync(String argumentString, UnaryOperator<JobReturn> callBack) {
		this.callBack = callBack;
	}
	@Override
	public String getName() {
		return name;
	}
	@Override
	public UnaryOperator<JobReturn> getCallback() {
		return callBack;
	}
	public BaseJobExecutor setName(String name) {
		this.name = name;
		return this;
	}
	@Override
	public void close() throws IOException {
		oStream.close();
	}
	@Override
	public IJobExecutor setStream(OutputStream os) {
		oStream = os;
		return this;
	}
}
