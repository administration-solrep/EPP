package fr.dila.st.core.exception;

public class PosteNotFoundException extends STException {

	private static final long	serialVersionUID	= 982438190999564138L;

	public PosteNotFoundException() {
		super();
	}

	public PosteNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public PosteNotFoundException(String message) {
		super(message);
	}

	public PosteNotFoundException(Throwable cause) {
		super(cause);
	}

}
