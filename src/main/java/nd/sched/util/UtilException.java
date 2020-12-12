package nd.sched.util;

public class UtilException extends RuntimeException{
    private static final long serialVersionUID = 3151837400662800162L;

    public UtilException(final String msg, Exception e) {
        super(msg, e);
    }
	public UtilException(Throwable cause) {
		super(cause);
	}
	public UtilException(String msg) {
		super(msg);
	}
}