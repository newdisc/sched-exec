package nd.sched.job;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BaseJobExecutor implements Closeable {
	private static final Logger logger = LoggerFactory.getLogger(BaseJobExecutor.class);
	protected String name;
	@JsonIgnore
	protected UnaryOperator<JobReturn> callBack;
	protected OutputStream oStream;

	public void writeSafe(final byte[] str) {
		try {
			oStream.write(str);
		} catch (IOException e) {
			logger.error("Could not write to outputStream: {}", str, e);
		}
	}
	public void executeAsync(String argumentString, UnaryOperator<JobReturn> callBack) {
		logger.debug("Execution parameters: {}", argumentString);
		this.callBack = callBack;
	}
	public String getName() {
		return name;
	}
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
	public BaseJobExecutor setStream(OutputStream os) {
		oStream = os;
		return this;
	}
	public OutputStream getStream() {
		return oStream;
	}
}
