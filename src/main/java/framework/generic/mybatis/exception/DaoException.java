package framework.generic.mybatis.exception;

public class DaoException extends RuntimeException {
	private static final long serialVersionUID = 304586860245241104L;

	public DaoException() {
	}

	public DaoException(String paramString, Throwable paramThrowable) {
		super(paramString, paramThrowable);
	}

	public DaoException(String paramString) {
		super(paramString);
	}

	public DaoException(Throwable paramThrowable) {
		super(paramThrowable);
	}
}