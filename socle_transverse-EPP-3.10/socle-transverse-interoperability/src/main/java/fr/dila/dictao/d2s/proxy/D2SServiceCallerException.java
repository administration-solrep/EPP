package fr.dila.dictao.d2s.proxy;

import fr.dila.dictao.proxy.DictaoServiceCallerException;

public class D2SServiceCallerException extends DictaoServiceCallerException {

	private static final long	serialVersionUID	= 1L;

	public D2SServiceCallerException() {
		super();
	}

	public D2SServiceCallerException(String message, Throwable cause) {
		super(message, cause);
	}

	public D2SServiceCallerException(String message) {
		super(message);
	}

	public D2SServiceCallerException(Throwable cause) {
		super(cause);
	}

}
