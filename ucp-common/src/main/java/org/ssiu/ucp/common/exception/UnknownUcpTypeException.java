package org.ssiu.ucp.common.exception;

/**
 * Convenient for throwing exceptions on switch {@link org.ssiu.ucp.common.mode}
 */
public class UnknownUcpTypeException extends IllegalArgumentException {

    public UnknownUcpTypeException() {
    }

    public UnknownUcpTypeException(String s) {
        super(s);
    }

    public UnknownUcpTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownUcpTypeException(Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = 8307853425207444515L;
}
