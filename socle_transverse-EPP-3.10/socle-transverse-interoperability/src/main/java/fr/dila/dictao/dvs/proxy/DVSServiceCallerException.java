package fr.dila.dictao.dvs.proxy;

import fr.dila.dictao.proxy.DictaoServiceCallerException;

public class DVSServiceCallerException extends DictaoServiceCallerException {

	private static final long	serialVersionUID	= 1L;

	public DVSServiceCallerException() {
		super();
	}

	public DVSServiceCallerException(String message, Throwable cause) {
		super(message, cause);
	}

	public DVSServiceCallerException(String message) {
		super(message);
	}

	public DVSServiceCallerException(Throwable cause) {
		super(cause);
	}

}
