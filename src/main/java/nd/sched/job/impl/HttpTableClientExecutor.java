package nd.sched.job.impl;

import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nd.data.stream.JSoupStream;
import nd.data.stream.StringToInputStream;
import nd.sched.job.BaseJobExecutor;
import nd.sched.job.JobReturn;
import nd.sched.job.JobReturn.JobStatus;

public class HttpTableClientExecutor extends BaseJobExecutor {
    private static final Logger logger = LoggerFactory.getLogger(
            "nd.sched.job.service.run." + HttpTableClientExecutor.class.getSimpleName());
        public static final String TYPE = "HTTP_Table";
        private JSoupStream stream;

        private String url;
        private String tableSelector;
        private String header = "th";
        private String column = "td";
        private String row = "tr";

        @Override
        public void executeAsync(String argumentString, UnaryOperator<JobReturn> callBack) {
        	super.executeAsync(argumentString, callBack);
            final JobReturn jr = new JobReturn();
			try {
				final StringToInputStream stis = StringToInputStream.toInputStream(url);
				stream = new JSoupStream();
				stream.setiStream(stis).setTableSelector(tableSelector)
					.setHeaderSelector(header).setRowsSelector(row)
					.setColumnSelector(column);
			} catch (Exception e) {
				final String msg = "Issue opening stream: " + url;
				logger.error(msg, e);
	            jr.setJobStatus(JobStatus.FAILURE);
	            callBack.apply(jr);
	            return;
			}
        	stream.loadStream();
        	stream.streamLines().map(ln -> {
        		final String str = ln.stream().collect(Collectors.joining("\",\"", "\"", "\""));
        		logger.info(str);
        		return str;
        	}).map(String::getBytes).forEach(this::writeSafe);
            jr.setJobStatus(JobStatus.SUCCESS);
            callBack.apply(jr);
        }

		public HttpTableClientExecutor setUrl(String url) {
			this.url = url;
			return this;
		}

		public HttpTableClientExecutor setTableSelector(String tableSelector) {
			this.tableSelector = tableSelector;
			return this;
		}

		public HttpTableClientExecutor setHeader(String header) {
			this.header = header;
			return this;
		}

		public HttpTableClientExecutor setColumn(String column) {
			this.column = column;
			return this;
		}

		public HttpTableClientExecutor setRow(String row) {
			this.row = row;
			return this;
		}
}
