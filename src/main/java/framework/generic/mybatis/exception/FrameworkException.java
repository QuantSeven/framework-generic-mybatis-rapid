package framework.generic.mybatis.exception;

public class FrameworkException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FrameworkException(String message, Throwable cause) {
		super(message, cause);
	}

	public FrameworkException(String message) {
		super(message);
	}

	public FrameworkException(Throwable cause) {
		super(null, cause);
	}
}
