package fr.dila.ss.api.birt;

import fr.dila.ss.api.exception.SSException;

public class BirtException extends SSException {
    private static final long serialVersionUID = 7089885408475122860L;

    public BirtException(String message) {
        super(message);
    }

    public BirtException(Throwable throwable) {
        super(throwable);
    }
}
